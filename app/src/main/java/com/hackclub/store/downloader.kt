package com.hackclub.store

interface Downloader {
    fun downloadFile(url:String): Long

}