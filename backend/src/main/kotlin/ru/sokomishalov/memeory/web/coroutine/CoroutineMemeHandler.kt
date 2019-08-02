package ru.sokomishalov.memeory.web.coroutine

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import ru.sokomishalov.memeory.service.db.MemeService
import ru.sokomishalov.memeory.util.MEMEORY_TOKEN_HEADER
import ru.sokomishalov.memeory.util.extensions.awaitResponse
import ru.sokomishalov.memeory.util.log.Loggable


/**
 * @author sokomishalov
 */
@Component
class CoroutineMemeHandler(
        private val service: MemeService
) : Loggable {

    suspend fun pageOfMemes(request: ServerRequest): ServerResponse {
        val page = request.pathVariable("page").toInt()
        val count = request.pathVariable("count").toInt()
        val token = request.headers().asHttpHeaders()[MEMEORY_TOKEN_HEADER]?.first()

        return service.pageOfMemes(page, count, token).awaitResponse()
    }
}
