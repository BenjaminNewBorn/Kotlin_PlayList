package com.example.playlist.activity

import android.arch.lifecycle.ViewModelProviders
import android.opengl.Visibility
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.playlist.R
import com.example.playlist.fragment.AddTracksForm
import com.example.playlist.fragment.FragmentSimilarTracks
import com.example.playlist.model.Track
import com.example.playlist.viewmodel.TrackViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_track_detail.*
import kotlin.math.min

class TrackDetailActivity : AppCompatActivity(){

    private lateinit var track: Track
    private lateinit var viewModel: TrackViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_detail)

        track = intent.extras!!.getSerializable("Track") as Track
        Log.e("FAVORITE", track.isFavorite.toString())

        viewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)

        this.loadUI(track)

        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.add(R.id.similar_holder, FragmentSimilarTracks(this, track.getName(), track.getArtist()), "SIMILAR_TRACK")
        ft.commit()

    }

    override fun onBackPressed() {
        this.finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.product_detail_menu,menu)
        if(this.track.isFavorite) {
            menu?.getItem(0)?.icon = getDrawable(R.drawable.ic_star_filled_24dp)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                this.finish()
                return true
            }
            R.id.action_favorite -> {
                if(this.track.isFavorite) {
                    item.icon = getDrawable(R.drawable.ic_star_border_yellow_24dp)
                    viewModel.removeFavorite(this.track.getName(), this.track.getArtist())

                } else {
                    item.icon = getDrawable(R.drawable.ic_star_filled_24dp)
                    viewModel.addFavorite(this.track)
                }
                this.track.isFavorite = !this.track.isFavorite

                return false
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadUI(track: Track) {
        detail_track_title.text = track.getName()
        val images = track.getImages()
        if (images.size > 0) {
            var imageIndex = min(3,images.size - 1)
            Picasso.with(this).load(track.getImages()[imageIndex]).into(detail_track_cover)
        }
        else {
            // eventually show image not available pic
        }
        detail_artist_tv.text = track.getArtist()
        if(track.getDuration() == -1) {
            detail_duration_tv.visibility = View.INVISIBLE
            detail_duration_label.visibility = View.INVISIBLE
        } else {
            detail_duration_tv.text = track.getDuration().toString()
        }

        if(track.getListeners() == -1) {
            detail_listener_label.visibility = View.INVISIBLE
            detail_listener_tv.visibility = View.INVISIBLE
        } else {
            detail_listener_tv.text = track.getListeners().toString()
        }

        if(track.getPlaycount() == -1) {
            detail_playcount_label.visibility = View.INVISIBLE
            detail_playcount_tv.visibility = View.INVISIBLE
        } else {
            detail_playcount_tv.text = track.getPlaycount().toString()
        }
    }


}