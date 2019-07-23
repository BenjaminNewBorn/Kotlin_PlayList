package com.example.playlist.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import android.util.Log
import com.example.playlist.db.DbSettings
import com.example.playlist.db.PlayListDatabaseHelper
import com.example.playlist.model.Track
import com.example.playlist.util.QueryUtils
import com.squareup.picasso.Picasso
import kotlin.math.min

class TrackViewModel(application: Application): AndroidViewModel(application) {
    private var _tracksList: MutableLiveData<ArrayList<Track>> = MutableLiveData()
    private var _playListDBHelper: PlayListDatabaseHelper = PlayListDatabaseHelper(application)
    private var fetchType: Int = 0

    fun getTopTracks(): MutableLiveData<ArrayList<Track>> {
        fetchType = 1
        loadProducts("?method=chart.gettoptracks&api_key=2292a31aac66cc5c5677d3638f03bac5&format=json")
        return _tracksList
    }

    fun searchTrackFromArtist(artistName: String): MutableLiveData<ArrayList<Track>> {
        fetchType = 2
        loadProducts("?method=artist.gettoptracks&api_key=2292a31aac66cc5c5677d3638f03bac5&format=json&artist=" + artistName)
        return _tracksList
    }

    fun getSimilarTracks(trackName: String, artistName: String): MutableLiveData<ArrayList<Track>> {
        fetchType = 3
        loadProducts("?method=track.getsimilar&api_key=2292a31aac66cc5c5677d3638f03bac5&format=json&artist=" + artistName + "&track=" + trackName)
        return _tracksList
    }

    private fun loadProducts(query: String) {
        TrackAsyncTask().execute(query)
    }

    @SuppressLint("StaticFieldLeak")
    inner class TrackAsyncTask: AsyncTask<String, Unit, ArrayList<Track>>() {
        override fun doInBackground(vararg params: String?): ArrayList<Track>? {
            return QueryUtils.fetchTrackData(params[0]!!, this@TrackViewModel.fetchType)
        }

        override fun onPostExecute(result: ArrayList<Track>?) {
            if (result == null) {
                Log.e("RESULTS", "No Results Found")
            }
            else {
                Log.e("RESULTS", result.toString())
                val favorites = this@TrackViewModel.loadFavorites()
                val resultList = ArrayList<Track>()
                for (item in result) {
                    for (fav in favorites) {
                        if (fav.getName() == item.getName() && item.getArtist() == fav.getArtist()) {
                            item.isFavorite = true
                        }
                    }
                    resultList.add(item)
                }
                _tracksList.value = resultList
            }
        }
    }

    fun getFavorites(): MutableLiveData<ArrayList<Track>> {
        val returnList = this.loadFavorites()
        this._tracksList.value = returnList
        return this._tracksList
    }

    private fun loadFavorites(): ArrayList<Track> {
        val favorites: ArrayList<Track> = ArrayList()
        val database = this._playListDBHelper.readableDatabase

        val cursor = database.query(
            DbSettings.DBPlayListEntry.TABLE,
            null,
            null, null, null, null, DbSettings.DBPlayListEntry.COL_TRACK_TITLE
        )

        while (cursor.moveToNext()) {
            val cursorId = cursor.getColumnIndex(DbSettings.DBPlayListEntry.ID)
            val cursorTitle = cursor.getColumnIndex(DbSettings.DBPlayListEntry.COL_TRACK_TITLE)
            val cursorArtist = cursor.getColumnIndex(DbSettings.DBPlayListEntry.COL_TRACK_ARTIST)
            val cursorPlayCount = cursor.getColumnIndex(DbSettings.DBPlayListEntry.COL_TRACK_PLAYCOUNT)
            val cursorDuration = cursor.getColumnIndex(DbSettings.DBPlayListEntry.COL_TRACK_DURATION)
            val cursorListeners = cursor.getColumnIndex(DbSettings.DBPlayListEntry.COL_TRACK_LISTENERS)

            val imageCursor = database.query(
                DbSettings.DBImageAssetEntry.TABLE,
                arrayOf(
                    DbSettings.DBImageAssetEntry.COL_URL
                ),
                "${DbSettings.DBImageAssetEntry.PLAY_ID}=?", arrayOf(cursor.getLong(cursorId).toString()), null, null, null
            )
            val images = ArrayList<String>()
            while (imageCursor.moveToNext()) {
                images.add(imageCursor.getString(imageCursor.getColumnIndex(DbSettings.DBImageAssetEntry.COL_URL)))
            }
            imageCursor.close()
            val track = Track(
                cursor.getString(cursorTitle),
                cursor.getInt(cursorDuration),
                cursor.getInt(cursorPlayCount),
                cursor.getInt(cursorListeners),
                images,
                cursor.getString(cursorArtist)
            )
            track.isFavorite = true
            favorites.add(track)
        }
        cursor.close()
        database.close()

        return favorites
    }

    fun removeFavorite(name: String, artistName: String,isFromResultList: Boolean = false) {
        val database: SQLiteDatabase = this._playListDBHelper.writableDatabase
        database.delete(DbSettings.DBPlayListEntry.TABLE,
            DbSettings.DBPlayListEntry.COL_TRACK_TITLE + " = \"" + name + "\" AND "
                    + DbSettings.DBPlayListEntry.COL_TRACK_ARTIST + " = \"" + artistName + "\"",
            null)
        database.close()

        var co = this._tracksList.value
        var targetId = 0
        if(co != null) {
            for(i in co.indices) {
                if(co[i].getName() == name && co[i].getArtist() == artistName) {
                    targetId = i
                }
            }
            if(isFromResultList) {
                co[targetId].isFavorite = false
            } else {
              co.remove(co[targetId])
            }
            this._tracksList.value = co
        }


    }

    fun addFavorite(track: Track){
        val database: SQLiteDatabase = this._playListDBHelper.writableDatabase

        val playListValues = ContentValues()
        playListValues.put(DbSettings.DBPlayListEntry.COL_TRACK_TITLE, track.getName())
        playListValues.put(DbSettings.DBPlayListEntry.COL_TRACK_ARTIST, track.getArtist())
        playListValues.put(DbSettings.DBPlayListEntry.COL_TRACK_DURATION, track.getDuration())
        playListValues.put(DbSettings.DBPlayListEntry.COL_TRACK_LISTENERS, track.getListeners())
        playListValues.put(DbSettings.DBPlayListEntry.COL_TRACK_PLAYCOUNT, track.getPlaycount())

        val playId = database.insertWithOnConflict(
            DbSettings.DBPlayListEntry.TABLE,
            null,
            playListValues,
            SQLiteDatabase.CONFLICT_REPLACE
        )

        val trackImages = track.getImages()
        if(trackImages.size == 0){

        }else{
            var imageIndex = min(3,trackImages.size - 1)
            val imageValues = ContentValues()
            imageValues.put(DbSettings.DBImageAssetEntry.PLAY_ID, playId)
            imageValues.put(DbSettings.DBImageAssetEntry.COL_URL, trackImages[imageIndex])
            database.insertWithOnConflict(
                DbSettings.DBImageAssetEntry.TABLE,
                null,
                imageValues,
                SQLiteDatabase.CONFLICT_REPLACE
            )
        }
        database.close()

    }


}