package ru.turev.hiltcorrutinescicerone.data.memory


object FreeMemory {

    fun getRemainingFreeMemory(): Long {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val allocatedMemory = totalMemory - freeMemory
        val maxMemory = runtime.maxMemory()

        return maxMemory - allocatedMemory
    }
}
