package com.exemple.avdownloader

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_episode_list.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main

class BookmarkActivity : Activity() {

    private val list = ArrayList<Show>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = CustomAdapter(list)

        update()
    }

    private fun update() {
        GlobalScope.launch(Dispatchers.Main) {
            progressBar.visibility = View.VISIBLE
            list.clear()
            list.addAll(async(Dispatchers.IO) { database.getAnimeList() }.await())
            recyclerView.adapter!!.notifyDataSetChanged()
            progressBar.visibility = View.GONE
        }
    }

}
