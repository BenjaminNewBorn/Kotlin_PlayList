package com.example.playlist.model

import java.io.Serializable

class Track() : Serializable {

    private var name: String = ""
    private var duration: Int = 0
    private var playcount: Int = 0
    private var listeners: Int = 0
    private var image: ArrayList<String> = ArrayList()
    private var artist: String = ""
    var isFavorite: Boolean = false


    constructor(
        name:String,
        duration:Int,
        playcount:Int,
        listeners:Int,
        image:ArrayList<String>,
        artist:String
    ):this(){
        this.name = name
        this.duration = duration
        this.playcount = playcount
        this.listeners = listeners
        this.image = image
        this.artist = artist
    }

    fun getName(): String {
        return this.name
    }

    fun getArtist(): String {
        return this.artist
    }

    fun getImages(): ArrayList<String> {
        return this.image
    }

    fun getDuration(): Int{
        return this.duration
    }

    fun getPlaycount(): Int{
        return this.playcount
    }

    fun getListeners(): Int{
        return this.listeners
    }


}
//    val id: String,
//    val artist: String,
