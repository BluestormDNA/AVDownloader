package com.exemple.avdownloader

import android.graphics.Color
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

const val INDEX: String = ""
const val SEARCH: String = "/browse?q="
const val MP4_EXT: String = ".mp4"
const val EMPTY: String = ""
const val NEXT_EP: String = "Próximo Episodio: "

val LIGHT_GREEN = Color.parseColor("#00E676")

@Parcelize
data class Show(val id: Int, val name: String, val url: String, val img: String, val isBookmarked: Int, val isAuto: Int) : Parcelable

data class Episode(val id: Int, val name: String, val num: String, val url: String?, val img: String?)