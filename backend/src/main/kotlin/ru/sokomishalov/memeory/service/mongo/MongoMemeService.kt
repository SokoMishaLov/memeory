package ru.sokomishalov.memeory.service.mongo

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.stereotype.Service
import reactor.bool.not
import reactor.core.publisher.Flux
import ru.sokomishalov.memeory.dto.MemeDTO
import ru.sokomishalov.memeory.entity.Meme
import ru.sokomishalov.memeory.repository.MemeRepository
import ru.sokomishalov.memeory.service.MemeService
import ru.sokomishalov.memeory.mapper.MemeMapper.Companion.INSTANCE as memeMapper

@Service
class MongoMemeService(
        private val repository: MemeRepository
) : MemeService {

    override fun saveMemesIfNotExist(memes: Flux<MemeDTO>): Flux<MemeDTO> {
        val memesToSaveFlux: Flux<Meme> = memes
                .filterWhen { !repository.existsById(it.id) }
                .map { memeMapper.toEntity(it) }

        return repository
                .saveAll(memesToSaveFlux)
                .map { memeMapper.toDto(it) }
    }

    override fun pageOfMemes(page: Int, count: Int): Flux<MemeDTO> {
        return repository
                .findMemeBy(PageRequest.of(page, count, Sort.by(DESC, "publishedAt")))
                .map { memeMapper.toDto(it) }
    }
}
