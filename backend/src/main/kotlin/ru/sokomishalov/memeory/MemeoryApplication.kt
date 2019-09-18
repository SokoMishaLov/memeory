package ru.sokomishalov.memeory

import org.slf4j.Logger
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.tools.agent.ReactorDebugAgent.init
import reactor.tools.agent.ReactorDebugAgent.processExistingClasses
import ru.sokomishalov.commons.core.log.loggerFor

/**
 * @author sokomishalov
 */

@SpringBootApplication
class MemeoryApplication

private val log: Logger = loggerFor(MemeoryApplication::class.java)

fun main(args: Array<String>) {
    initReactorDebugTools()
    runApplication<MemeoryApplication>(*args)
}

private fun initReactorDebugTools() {
    try {
        init()
        processExistingClasses()

        log.info("ReactorDebugAgent started")
    } catch (e: Throwable) {
        log.warn("ReactorDebugAgent failed to start: ${e.message}")
    }
}
