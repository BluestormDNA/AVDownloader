package com.exemple.avdownloader

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable

const val INDEX: String = ""
const val SEARCH: String = "/browse?q="
const val MP4_EXT: String = ".mp4"
const val EMPTY: String = ""
const val NEXT_EP: String = "Próximo Episodio: "

val LIGHT_GREEN = Color.parseColor("#00E676")

data class Show(val id: Int, val name: String, val url: String, val img: String, val isBookmarked: Int, val isAuto: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(url)
        parcel.writeString(img)
        parcel.writeInt(isBookmarked)
        parcel.writeInt(isAuto)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Show> {
        override fun createFromParcel(parcel: Parcel): Show {
            return Show(parcel)
        }

        override fun newArray(size: Int): Array<Show?> {
            return arrayOfNulls(size)
        }
    }
}

data class Episode(val id: Int, val name: String, val num: String, val url: String?, val img: String?)