package com.exemple.avdownloader

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jsoup.Jsoup

//TODO refactor an unique Download manager?
class Downloader(val context: Context) {

    private fun parseVideoIndex(url: String?): String? {
        Log.d("DEBUG", url)

        val urlHTML = Jsoup.connect(url).get().html()
        val regexRVServer = "video\\[\\d]\\s=\\s\\'<iframe.+?src=\"(.+?server=rv.+?)\"".toRegex()
        val rvRedirectorURL = regexRVServer.find(urlHTML)?.groupValues?.get(1)
        Log.d("DEBUG", rvRedirectorURL)

        val rvRedirectorHTML = Jsoup.connect(rvRedirectorURL).get().html()
        val reg = "window.location.href = \"(.*)\";".toRegex()
        val rvURL = reg.find(rvRedirectorHTML)?.groupValues?.get(1)
        Log.d("DEBUG", rvURL)

        val rvHTML = Jsoup.connect(rvURL).get().html()
        val regexRVMain = "<link rel=\"canonical\" href=\"(.*)\"> ".toRegex()
        val rvCanonicalURL = regexRVMain.find(rvHTML)?.groupValues?.get(1)
        Log.d("DEBUG", rvCanonicalURL)

        val vHTML = Jsoup.connect(rvCanonicalURL).get().html()
        val regexV = "<source src=\"(.*)\" type=\"video/mp4\"".toRegex()
        val v = regexV.find(vHTML)?.groupValues?.get(1)
        Log.d("DEBUG", v)

        return v
    }

    private fun downloadFile(e: Episode, v: String?) {
        val downloadManager = context.getSystemService(Activity.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(v)).apply {
            allowScanningByMediaScanner()
            setTitle("${e.name} ${e.num}")
            //setDescription(e.num)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${e.name} ${e.num}$MP4_EXT")
        }

        val enqueue = downloadManager.enqueue(request)
        //enqueue
    }

    fun handleDownload(e: Episode) {
        launch(UI) {
            val v = async { parseVideoIndex(e.url) }.await()
            downloadFile(e, v)
        }
    }

}
