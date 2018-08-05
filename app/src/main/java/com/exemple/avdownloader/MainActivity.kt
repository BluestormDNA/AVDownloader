package com.exemple.avdownloader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jsoup.Jsoup

class MainActivity : Activity() {

    private val list = ArrayList<Show>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = CustomAdapter(list)

        update(INDEX + SEARCH)
    }

    private fun parseIndex(url: String): ArrayList<Show> {
        val list = ArrayList<Show>()
        val doc = Jsoup.connect(url).get()
        val articles = doc.select("article")

        articles.forEach {
            val name = it.select("h3").text()
            val url = it.select("a").attr("href")
            val img = it.select("img").attr("src")

            val regexId = "anime/(.*)/".toRegex()
            val id = regexId.find(url)?.groupValues?.get(1)!!.toInt()

            list.add(Show(id, name, INDEX + url, img, 0, 0))
        }
        return list
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val actionView = menu.findItem(R.id.action_search).actionView as SearchView
        actionView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean = false

            override fun onQueryTextSubmit(query: String?): Boolean {
                update(INDEX + SEARCH + query)
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_bookmark -> {
                val intent = Intent(this, BookmarkActivity::class.java)
                this.startActivity(intent)
            }
        //R.id.action_search -> search()
        }
        return true
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
}


