package ru.sokomishalov.memeory.api.web

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders.ACCESS_CONTROL_MAX_AGE
import org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
import org.springframework.http.MediaType.IMAGE_PNG
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.sokomishalov.commons.core.images.getImageByteArray
import ru.sokomishalov.commons.spring.cache.CacheService
import ru.sokomishalov.memeory.core.dto.ChannelDTO
import ru.sokomishalov.memeory.core.util.consts.CHANNEL_LOGO_CACHE_KEY
import ru.sokomishalov.memeory.core.util.consts.DELIMITER
import ru.sokomishalov.memeory.db.ChannelService
import ru.sokomishalov.memeory.providers.ProviderFactory
import org.springframework.http.ResponseEntity.ok as responseEntityOk

/**
 * @author sokomishalov
 */
@RestController
@RequestMapping("/channels")
class ChannelController(private val channelService: ChannelService,
                        private val providerFactory: ProviderFactory,
                        private val cache: CacheService,
                        @Qualifier("placeholder")
                        private val placeholder: ByteArray
) {

    @GetMapping("/list")
    suspend fun all(): List<ChannelDTO> =
            channelService.findAll()

    @GetMapping("/list/enabled")
    suspend fun enabled(): List<ChannelDTO> =
            channelService.findAllEnabled()

    @PostMapping("/enable")
    suspend fun enable(@RequestBody channelIds: List<String>) =
            channelService.toggleEnabled(true, *channelIds.toTypedArray())

    @PostMapping("/disable")
    suspend fun disable(@RequestBody channelIds: List<String>) =
            channelService.toggleEnabled(false, *channelIds.toTypedArray())

    @PostMapping("/add")
    suspend fun add(@RequestBody channel: ChannelDTO): ChannelDTO? =
            channelService.save(channel)

    @GetMapping("/logo/{channelId}")
    suspend fun logo(@PathVariable channelId: String): ResponseEntity<ByteArray> {
        val logoByteArray = cache.get(CHANNEL_LOGO_CACHE_KEY, channelId) {
            val url = runCatching {
                val channel = channelService.findById(channelId)
                val service = providerFactory.getService(channel.provider)
                service?.getLogoUrl(channel)
            }.getOrNull()
            getImageByteArray(url, orElse = placeholder)
        }

        return responseEntityOk()
                .contentType(IMAGE_PNG)
                .contentLength(logoByteArray.size.toLong())
                .header(CONTENT_DISPOSITION, "attachment; filename=$channelId${DELIMITER}logo.png")
                .header(ACCESS_CONTROL_MAX_AGE, "31536000", "public")
                .body(logoByteArray)
    }
}