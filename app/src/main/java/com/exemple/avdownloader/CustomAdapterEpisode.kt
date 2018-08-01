package com.exemple.avdownloader

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.epi_layout.view.*

class CustomAdapterEpisode(val list: ArrayList<Episode>) : RecyclerView.Adapter<CustomAdapterEpisode.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.epi_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.textView.text = "${list[position].name} ${list[position].num}"

        GlideApp.with(holder.itemView.context)
                .load(list[position].img)
                .override(400)
                .into(holder.itemView.imageView)

        //TODO swipe anime to mark unmark viewed
        if (holder.itemView.context.database.isClicked(list[position])) {
            holder.itemView.imageViewRibbon.setBackgroundColor(LIGHT_GREEN)
        } else {
            holder.itemView.imageViewRibbon.setBackgroundColor(Color.WHITE)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
            itemView.imageViewRibbon.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            when (v) {
                itemView.imageViewRibbon -> { }
                else -> Downloader(v.context).handleDownload(list[adapterPosition])
            }
            if (!itemView.context.database.isClicked(list[adapterPosition])) {
                v.context.database.insertEpisode(list[adapterPosition])
                itemView.imageViewRibbon.setBackgroundColor(LIGHT_GREEN)
            }

        }

    }

}
