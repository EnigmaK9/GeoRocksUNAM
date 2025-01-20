// File path: app/src/main/java/com/enigma/georocks/data/DiskCacheManager.kt

package com.enigma.georocks.data

import android.content.Context
import android.util.Log
import java.io.File

class DiskCacheManager(private val context: Context) {

    private val cacheFileName = "rocksData.json"

    fun saveData(jsonString: String) {
        try {
            val file = getCacheFile()
            file.writeText(jsonString)
            Log.d("DiskCacheManager", "Data saved to cache.")
        } catch (e: Exception) {
            Log.e("DiskCacheManager", "Error saving data: ${e.localizedMessage}", e)
        }
    }

    fun loadData(): String? {
        return try {
            val file = getCacheFile()
            if (!file.exists()) {
                null
            } else {
                file.readText()
            }
        } catch (e: Exception) {
            Log.e("DiskCacheManager", "Error reading data: ${e.localizedMessage}", e)
            null
        }
    }

    fun removeCachedData() {
        try {
            val file = getCacheFile()
            if (file.exists()) {
                file.delete()
                Log.d("DiskCacheManager", "Cache file removed.")
            }
        } catch (e: Exception) {
            Log.e("DiskCacheManager", "Error removing cached data: ${e.localizedMessage}", e)
        }
    }

    private fun getCacheFile(): File {
        val dir = context.filesDir
        return File(dir, cacheFileName)
    }
}
