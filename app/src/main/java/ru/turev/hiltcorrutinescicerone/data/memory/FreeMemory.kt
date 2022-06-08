package ru.turev.hiltcorrutinescicerone.data.memory

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FreeMemory @Inject constructor(@ApplicationContext private val context: Context) {

    fun getRemainingFreeMemory(): Long {
        val memoryInfo = ActivityManager.MemoryInfo()
        val activityManager: ActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memoryInfo)
        val availableMegs = memoryInfo.availMem / 0x100000L // Mb
        //  Log.d("qqq", "availableMegs:  $availableMegs")
        val availableBytes = memoryInfo.availMem // byte
        Log.d("qqq", "availableBytes:  $availableBytes")
        return availableBytes
    }
}
