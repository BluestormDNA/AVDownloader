package com.exemple.avdownloader

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_episode_list.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jsoup.Jsoup
import java.nio.charset.Charset

class EpisodeListActivity : Activity() {

    private val list = ArrayList<Episode>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_list)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CustomAdapterEpisode(list)

        val show = intent.getParcelableExtra("item") as Show

        askPermissions()

        update(show.url)
    }

    private fun update(url: String) {
        launch(UI) {
            progressBar.visibility = View.VISIBLE
            list.clear()
            list.addAll(async { parseIndex(url) }.await())
            recyclerView.adapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
        }
    }

    private fun parseIndex(url: String): ArrayList<Episode> {
        val list = ArrayList<Episode>()

        val doc = Jsoup.connect(url).get().toString()
        val regexInfo = "var anime_info = \\[\"(.*)\"];".toRegex()
        val regexEpisodes = "var episodes = (.*);".toRegex()

        val rawInfo = regexInfo.find(doc)?.groupValues?.get(1)
        val rawEpisodes = regexEpisodes.find(doc)?.groupValues?.get(1)

        println(rawInfo)
        println(rawEpisodes)

        val info = rawInfo!!.split("\",\"")
        val episodes = rawEpisodes!!.replace("[\"\\[\\]]+".toRegex(), "").split(",")
        println(info)
        val (id, name, urlTitle) = info
        val date = if (info.size > 3) info[3] else null

        println("INFO $id $name $urlTitle $date")
        println(episodes)

        if (null != date) list.add(Episode(1, NEXT_EP + date, EMPTY, null, null))

        for (i in episodes.indices step 2) {
            val num = episodes[i]
            val url = "$INDEX/ver/${episodes[i + 1]}/$urlTitle-$num"
            val img = "$INDEX/uploads/animes/screenshots/$id/$num/th_3.jpg"

            list.add(Episode(id.toInt(), name, num, url, img))
        }
        return list
    }

    private fun askPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We have permissions: do all the things!!!
                try {
                    // Hacer cosas
                } catch (e: SecurityException) {
                    // fail
                }

            } else {
                //
            }
        }
    }
}
