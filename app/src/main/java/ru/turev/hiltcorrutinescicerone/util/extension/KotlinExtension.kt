package ru.turev.hiltcorrutinescicerone.util.extension

inline fun <reified T> Any?.castTo(): T? {
    return this as? T
}

fun invokeIf(predicate: () -> Boolean, block: () -> Unit) {
    if (predicate()) {
        block()
    }
}


