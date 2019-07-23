package com.example.playlist.util

import android.text.TextUtils
import android.util.Log
import com.example.playlist.model.Track
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset
import kotlin.math.max
import kotlin.math.min

class QueryUtils {
    companion object {
        private val LogTag = this::class.java.simpleName
        private const val BaseURL = "http://ws.audioscrobbler.com/2.0/" // localhost URL

        fun fetchTrackData(jsonQueryString: String, fetchType:Int): ArrayList<Track>? {
            val url: URL? = createUrl("${this.BaseURL}$jsonQueryString")

            var jsonResponse: String? = null
            try {
                jsonResponse = makeHttpRequest(url)
            }
            catch (e: IOException) {
                Log.e(this.LogTag, "Problem making the HTTP request.", e)
            }

            return extractTrackFromJson(jsonResponse, fetchType)
        }


        private fun createUrl(stringUrl: String): URL? {
            var url: URL? = null
            try {
                url = URL(stringUrl)
            }
            catch (e: MalformedURLException) {
                Log.e(this.LogTag, "Problem building the URL.", e)
            }

            return url
        }


        private fun makeHttpRequest(url: URL?): String {
            var jsonResponse = ""

            if (url == null) {
                return jsonResponse
            }

            var urlConnection: HttpURLConnection? = null
            var inputStream: InputStream? = null
            try {
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.readTimeout = 10000 // 10 seconds
                urlConnection.connectTimeout = 15000 // 15 seconds
                urlConnection.requestMethod = "GET"
                urlConnection.connect()

                if (urlConnection.responseCode == 200) {
                    inputStream = urlConnection.inputStream
                    jsonResponse = readFromStream(inputStream)
                }
                else {
                    Log.e(this.LogTag, "Error response code: ${urlConnection.responseCode}")
                }
            }
            catch (e: IOException) {
                Log.e(this.LogTag, "Problem retrieving the product data results: $url", e)
            }
            finally {
                urlConnection?.disconnect()
                inputStream?.close()
            }

            return jsonResponse
        }

        private fun extractTrackFromJson(trackJson: String?, fetchType: Int): ArrayList<Track>? {
            if (TextUtils.isEmpty(trackJson)) {
                return null
            }

//            Log.e("json",trackJson)
            var trackList = ArrayList<Track>()
            var jsonKey = ""
            if(fetchType == 1) {
                jsonKey = "tracks"
            } else if(fetchType == 2) {
                jsonKey = "toptracks"
            } else if(fetchType == 3){
                jsonKey = "similartracks"
            }

            try {
                val baseJsonResponse = JSONObject(trackJson).getJSONObject(jsonKey).getJSONArray("track")
                var max_count = min(baseJsonResponse.length(),20)
                if(fetchType == 3) {
                    max_count = min(max_count, 5)
                }
                for(i in 0 until max_count){
                    val trackObject = baseJsonResponse.getJSONObject(i)

                    //Images
                    val images = returnValueOrDefault<JSONArray>(trackObject, "image") as JSONArray?
                    val imageArrayList = ArrayList<String>()
                    if (images != null) {
                        for (j in 0 until images.length()) {
                            imageArrayList.add(returnValueOrDefault<String>(images.getJSONObject(j), "#text") as String)
                        }
                    }
                    var artistList = trackObject.getJSONObject("artist")

                    Log.e("Images",imageArrayList.toString())
                    trackList.add(Track(
                        returnValueOrDefault<String>(trackObject, "name") as String,
                        returnValueOrDefault<Int>(trackObject, "duration") as Int,
                        returnValueOrDefault<Int>(trackObject, "playcount") as Int,
                        returnValueOrDefault<Int>(trackObject, "listeners") as Int,
                        imageArrayList,
                        returnValueOrDefault<String>(artistList, "name") as String
                    ))
                }
            } catch (e: JSONException) {
                Log.e(this.LogTag, "Problem parsing the Track JSON results", e)
            }
            return trackList
        }


        private fun readFromStream(inputStream: InputStream?): String {
            val output = StringBuilder()
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
                val reader = BufferedReader(inputStreamReader)
                var line = reader.readLine()
                while (line != null) {
                    output.append(line)
                    line = reader.readLine()
                }
            }

            return output.toString()
        }

        private inline fun <reified T> returnValueOrDefault(json: JSONObject, key: String): Any? {
            when (T::class) {
                String::class -> {
                    return if (json.has(key)) {
                        json.getString(key)
                    } else {
                        ""
                    }
                }
                Int::class -> {
                    return if (json.has(key)) {
                        json.getInt(key)
                    }
                    else {
                        return -1
                    }
                }
                Double::class -> {
                    return if (json.has(key)) {
                        json.getDouble(key)
                    }
                    else {
                        return -1.0
                    }
                }
                Long::class -> {
                    return if (json.has(key)) {
                        json.getLong(key)
                    }
                    else {
                        return (-1).toLong()
                    }
                }
                JSONObject::class -> {
                    return if (json.has(key)) {
                        json.getJSONObject(key)
                    }
                    else {
                        return null
                    }
                }
                JSONArray::class -> {
                    return if (json.has(key)) {
                        json.getJSONArray(key)
                    }
                    else {
                        return null
                    }
                }
                else -> {
                    return null
                }
            }
        }
    }
}