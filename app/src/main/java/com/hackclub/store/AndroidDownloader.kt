package com.hackclub.store

import android.app.DownloadManager
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.net.toUri

class AndroidDownloader(
    private val context: Context
):Downloader {
    @RequiresApi(Build.VERSION_CODES.M)
    private val downloadManager = context.getSystemService(DownloadManager::class.java)


    @RequiresApi(Build.VERSION_CODES.M)
    override fun downloadFile(url: String): Long{
        var mime = "image/svg+xml"
        if(url.contains("apk")){
            mime = "application/vnd.android.package-archive"
        }
        val request = DownloadManager.Request(url.toUri())
            .setMimeType(mime)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("app.apk")
            .setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, "app.apk")
        return downloadManager.enqueue(request)
    }
}