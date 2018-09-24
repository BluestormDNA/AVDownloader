package com.exemple.avdownloader

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import org.jsoup.Jsoup

class Downloader(val context: Context) {

    companion object {
        private var instance: Downloader? = null

        @Synchronized
        fun getInstance(ctx: Context): Downloader {
            if (instance == null) {
                instance = Downloader(ctx.applicationContext)
            }
            return instance!!
        }
    }

    private fun parseVideoIndex(url: String?): String? {
        Log.d("Main URL", url)

        val urlHTML = Jsoup.connect(url).get().html()
        val regexRVServer = "video\\[\\d]\\s=\\s\\'<iframe.+?src=\"(.+?server=rv.+?)\"".toRegex()
        val rvRedirectorURL = regexRVServer.find(urlHTML)?.groupValues?.get(1)
        Log.d("RV Redirector", rvRedirectorURL)

        val rvRedirectorHTML = Jsoup.connect(rvRedirectorURL).get().html()
        val reg = "window.location.href = \"(.*)\";".toRegex()
        val rvURL = reg.find(rvRedirectorHTML)?.groupValues?.get(1)
        Log.d("RV URL", rvURL)

        val rvHTML = Jsoup.connect(rvURL).get().html()
        val regexRVMain = "<link rel=\"canonical\" href=\"(.*)\"".toRegex()
        val rvCanonicalURL = regexRVMain.find(rvHTML)?.groupValues?.get(1)
        Log.d("RV Canonical URL", rvCanonicalURL)

        val vHTML = Jsoup.connect(rvCanonicalURL).get().html()
        val regexV = "<source src=\"(.*)\" type=\"video/mp4\"".toRegex()
        val v = regexV.find(vHTML)?.groupValues?.get(1)
        Log.d("V", v)

        return v
    }

    private fun downloadFile(name: String, num: String, url: String) {
        val downloadManager = context.getSystemService(Activity.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(url)).apply {
            allowScanningByMediaScanner()
            setTitle("${name} ${num}")
            //setDescription(e.num)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${name} ${num}$MP4_EXT")
        }

        val enqueue = downloadManager.enqueue(request)
        Log.d("Dowloader Instance", this.toString())
    }

    fun handleDownload(e: Episode) {
        GlobalScope.launch(Dispatchers.Main) {
            val url = async(Dispatchers.IO) { parseVideoIndex(e.url) }.await()!!
            downloadFile(e.name, e.num, url)
        }
    }
}

// Access property for Context
val Context.downloader: Downloader
    get() = Downloader.getInstance(applicationContext)