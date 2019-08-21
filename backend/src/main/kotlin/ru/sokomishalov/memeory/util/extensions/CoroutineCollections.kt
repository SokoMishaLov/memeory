package ru.sokomishalov.memeory.util.extensions

import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext


/**
 * @author sokomishalov
 */

suspend inline fun <T> Iterable<T>.aForEach(noinline block: suspend (T) -> Unit) = withContext(Unconfined) {
    map { async { block(it) } }.forEach { it.await() }
}

suspend inline fun <T, R> Iterable<T>.aMap(noinline transform: suspend (T) -> R): List<R> = withContext(Unconfined) {
    map { async { transform(it) } }.map { it.await() }
}

suspend inline fun <T> Iterable<T>.aFilter(crossinline predicate: suspend (T) -> Boolean): List<T> = withContext(Unconfined) {
    val destination = ArrayList<T>()
    map { async { if (predicate(it)) destination.add(it) } }.forEach { it.await() }
    destination
}

suspend inline fun <T> Array<T>.aFilter(crossinline predicate: suspend (T) -> Boolean): List<T> {
    return this.toList().aFilter(predicate)
}
