package ru.turev.hiltcorrutinescicerone.data.memory

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FreeMemory @Inject constructor() {

    fun getRemainingFreeMemory(): Long {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val allocatedMemory = totalMemory - freeMemory
        val maxMemory = runtime.maxMemory()

        return maxMemory - allocatedMemory
    }
}
