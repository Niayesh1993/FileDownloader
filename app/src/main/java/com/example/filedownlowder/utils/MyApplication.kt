package com.example.filedownlowder.utils

import android.app.Application
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig


/**
 * Created by Zohre Niayeshi on 01,August,2020 niayesh1993@gmail.com
 **/
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val config = PRDownloaderConfig.newBuilder()
            .setDatabaseEnabled(true)
            .build()
        PRDownloader.initialize(this, config)
    }
}