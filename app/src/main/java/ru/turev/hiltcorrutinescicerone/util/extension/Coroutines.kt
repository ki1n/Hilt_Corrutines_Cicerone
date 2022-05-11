package ru.turev.hiltcorrutinescicerone.util.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

suspend fun <T> default(block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.Default, block)
}

suspend fun <T> io(block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.IO, block)
}

suspend fun <T, R> List<T>.mapAsync(block: suspend (T) -> R): List<R> {
    return io { this@mapAsync.map { async { block(it) } }.awaitAll() }
}
