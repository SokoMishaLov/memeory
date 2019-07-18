package ru.sokomishalov.memeory.service.db

import reactor.core.publisher.Mono
import ru.sokomishalov.memeory.dto.ProfileDTO

interface ProfileService {

    fun saveProfileInfoIfNecessary(profile: ProfileDTO): Mono<ProfileDTO>

}
