package com.example.playlist.db

import android.provider.BaseColumns
class DbSettings {
    companion object {
        const val DB_NAME = "playList.db"
        const val DB_VERSION = 1
    }

    class DBPlayListEntry: BaseColumns{
        companion object {
            const val TABLE = "playList"
            const val ID = BaseColumns._ID
            const val COL_TRACK_TITLE = "title"
            const val COL_TRACK_PLAYCOUNT = "playcount"
            const val COL_TRACK_DURATION = "duration"
            const val COL_TRACK_ARTIST = "artist"
            const val COL_TRACK_LISTENERS = "listeners"
//            const val COL_TRACK_IMG_ID = "image_id"
        }
    }

    class DBImageAssetEntry: BaseColumns {
        companion object {
            const val TABLE = "images"
            const val ID = BaseColumns._ID
            const val PLAY_ID = "play_id"
            const val COL_URL = "cloud_url"
        }
    }


}