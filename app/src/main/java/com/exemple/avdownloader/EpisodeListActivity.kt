package com.exemple.avdownloader

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_episode_list.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.android.UI
import org.jsoup.Jsoup

class EpisodeListActivity : Activity() {

    private val list = ArrayList<Episode>()
    private lateinit var show: Show

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_list)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CustomAdapterEpisode(list)

        show = intent.getParcelableExtra("item") as Show

        askPermissions()

        update(show.url)
    }

    private fun update(url: String) {
        GlobalScope.launch(Dispatchers.Main) {
            progressBar.visibility = View.VISIBLE
            list.clear()
            list.addAll(async(Dispatchers.IO) { parseIndex(url) }.await())
            recyclerView.adapter!!.notifyDataSetChanged()
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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_episode, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_massDownload -> massDownload()
            R.id.action_subscribeUpdates -> subscribeUpdates()
        }
        return true
    }

    private fun massDownload() {
        //todo put a visual warning dialog or something with number of files to download
        //todo probably revamp e.url date hack cleanup
        //todo is next ep date unnecesary if theres subscribe?
        //todo put it on other place? outside the list? on actionBar?
        for (e in list) when {
            e.url != null -> application.downloader.handleDownload(e)
        }
    }

    private fun subscribeUpdates() {
        this.database.subscribeAnime(show)
        //todo work manager madness
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
