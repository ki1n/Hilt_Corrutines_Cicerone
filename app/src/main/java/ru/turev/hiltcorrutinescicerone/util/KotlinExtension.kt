package ru.turev.hiltcorrutinescicerone.util

inline fun <reified T> Any?.unsafeCastTo(): T {
    return this as T
}

inline fun <reified T> Any?.castTo(): T? {
    return this as? T
}

val String.Companion.empty
    get() = ""

val String.Companion.space
    get() = " "

val String.Companion.dot
    get() = "."

fun invokeIf(predicate: () -> Boolean, block: () -> Unit) {
    if (predicate()) {
        block()
    }
}

fun invokeIfNot(predicate: () -> Boolean, block: () -> Unit) {
    if (!predicate()) {
        block()
    }
}

inline fun Any?.isNull(): Boolean {
    return this == null
}

inline fun Any?.isNotNull(): Boolean {
    return this != null
}
