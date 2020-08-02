package com.example.filedownlowder.utils

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import java.io.File
import java.util.*


/**
 * Created by Zohre Niayeshi on 01,August,2020 niayesh1993@gmail.com
 **/
class Utils {

    fun getRootDirPath(context: Context): String? {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val file: File = ContextCompat.getExternalFilesDirs(
                context.applicationContext,
                null
            ).get(0)
            file.absolutePath
        } else {
            context.applicationContext.filesDir.absolutePath
        }
    }

    fun getProgressDisplayLine(
        currentBytes: Long,
        totalBytes: Long
    ): String? {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes)
    }

    private fun getBytesToMBString(bytes: Long): String {
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00))
    }
}