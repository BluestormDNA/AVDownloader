package com.exemple.avdownloader

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class MyDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "MyDatabase", null, 1) {
    companion object {
        private var instance: MyDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MyDatabaseOpenHelper {
            if (instance == null) {
                instance = MyDatabaseOpenHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable("Anime", true,
                "_id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "name" to TEXT,
                "url" to TEXT,
                "img" to TEXT,
                "isBookmarked" to INTEGER,
                "isAuto" to INTEGER)

        db.createTable("Episode", true,
                "_id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "anime_id" to INTEGER,
                "num" to TEXT,
                "isClicked" to INTEGER,
                "isPending" to INTEGER)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable("Anime", true)
        db.dropTable("Episode", true)
    }

    fun insertAnime(s: Show) {
        writableDatabase.insert("Anime",
                "_id" to s.id,
                "name" to s.name,
                "url" to s.url,
                "img" to s.img,
                "isBookmarked" to 1,
                "isAuto" to 0
        )
    }

    fun deleteAnime(e: Show) {
        writableDatabase.delete("Anime",
                "_id = {id}", "id" to e.id)
    }

    fun getAnimeList(): List<Show> {
        return readableDatabase.select("Anime")
                .parseList(classParser())
    }

    fun insertEpisode(e: Episode) {
        writableDatabase.insert("Episode",
                "anime_id" to e.id,
                "num" to e.num,
                "isClicked" to 1
        )
    }

    fun isBookmarked(s: Show): Boolean {
        val select = readableDatabase.select("Anime", "isBookmarked")
                .whereArgs(
                        "(_id = {s.id})",
                        "s.id" to s.id
                )
                .exec { parseOpt(IntParser) }

        return intToBool(select)
    }

    fun isClicked(e: Episode): Boolean {
        val select = readableDatabase.select("Episode", "isClicked")
                .whereArgs(
                        "(anime_id = {e.id}) and (num = {e.num})",
                        "e.id" to e.id,
                        "e.num" to e.num
                )
                .exec { parseOpt(IntParser) }

        return intToBool(select)
    }

    private fun intToBool(i: Int?): Boolean {
        return when (i) {
            1 -> true
            else -> false
        }
    }

    fun subscribeAnime(s: Show) {

    }
}

// Access property for Context
val Context.database: MyDatabaseOpenHelper
    get() = MyDatabaseOpenHelper.getInstance(applicationContext)