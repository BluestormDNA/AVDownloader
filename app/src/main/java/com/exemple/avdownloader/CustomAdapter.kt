package com.exemple.avdownloader

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_layout.view.*
import android.content.Intent
import android.database.sqlite.SQLiteException

class CustomAdapter(val list: ArrayList<Show>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.textView?.text = list[position].name

        GlideApp.with(holder.itemView.context)
                .load(list[position].img)
                .override(300)
                .into(holder.itemView.imageView)

        starToggle(holder.itemView, position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun starToggle(view: View, adapterPosition: Int){
        if(view.context.database.isBookmarked(list[adapterPosition])){
            view.imageViewToggle.setImageResource(R.drawable.ic_action_star)
        } else {
            view.imageViewToggle.setImageResource(R.drawable.ic_action_star_border)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
            itemView.imageViewToggle.setOnClickListener(this)
        }
        // TODO swipe anime to mark or unmark viewed
        override fun onClick(v: View) {
            when(v){
                itemView.imageViewToggle -> {
                    //TODO this code duplicates db acces with starToggle but it works ¯\_(ツ)_/¯
                    if(v.context.database.isBookmarked(list[adapterPosition])){
                        v.context.database.deleteAnime(list[adapterPosition])
                    } else {
                        v.context.database.insertAnime(list[adapterPosition])
                    }
                    //
                    starToggle(v, adapterPosition)
                }
                else -> {
                    val intent = Intent(v.context, EpisodeListActivity::class.java)
                    intent.putExtra("item", list[adapterPosition])
                    v.context.startActivity(intent)
                }
            }

        }

    }

}