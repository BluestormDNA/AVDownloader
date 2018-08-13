package com.exemple.avdownloader

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jsoup.Jsoup

//TODO refactor an unique Download manager?
class Downloader(val context: Context) {

    private fun parseVideoIndex(url: String?): String? {
        println(url)
        val doc = Jsoup.connect(url).get().html()

        val regex = "video\\[\\d]\\s=\\s\\'<iframe.+?src=\"(.+?server=rv.+?)\"".toRegex()
        val html = regex.find(doc)?.groupValues?.get(1)
        //Log.d("DEBUG", html)

        val docV = Jsoup.connect(html).get().html()

        val reg = "window.location.href = \"(.*)\";".toRegex()
        val vLink = reg.find(docV)?.groupValues?.get(1)
        //Log.d("DEBUG", vLink)

        val vHtml = Jsoup.connect(vLink).get().html()
        //Log.d("DEBUG", vHtml)
        val r = "<source src=\"(.*)\" type".toRegex()

        val v = r.find(vHtml)?.groupValues?.get(1)
        //Log.d("DEBUG", v)
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
