package com.example.playlist.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.playlist.R
import com.example.playlist.activity.TrackDetailActivity
import com.example.playlist.model.Track
import com.example.playlist.viewmodel.TrackViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.add_tracks_item.view.*
import kotlinx.android.synthetic.main.fragment_add_track_list.*
import kotlinx.android.synthetic.main.similar_tracks_items.view.*
import kotlinx.android.synthetic.main.similar_tracks_list.*
import kotlin.math.min

@SuppressLint("ValidFragment")
class FragmentSimilarTracks(context: Context, trackName:String, trackArtist: String): Fragment() {
    private  var parentContext = context
    private lateinit var viewModel: TrackViewModel
    private var similarInitialized: Boolean = false
    private var adapter = SimilarTracksAdapter()
    private var queryTrackName: String = trackName
    private var queryTrackArtist: String = trackArtist

    private var trackList:ArrayList<Track> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.similar_tracks_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        similar_recycler.layoutManager = GridLayoutManager(this.parentContext, 5)
        similar_recycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
        viewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)

        val observer = Observer<ArrayList<Track>> {
            similar_recycler.adapter = adapter
            var result = DiffUtil.calculateDiff(object: DiffUtil.Callback(){
                override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
                    return trackList[p0].getName() == trackList[p1].getName()
                }

                override fun getOldListSize(): Int {
                    return trackList.size
                }

                override fun getNewListSize(): Int {
                    if (it == null) {
                        return 0
                    }
                    return it.size
                }

                override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
                    return trackList[p0] == trackList[p1]
                }
            })

            result.dispatchUpdatesTo(adapter)
            trackList = it ?: ArrayList()
        }

        viewModel.getSimilarTracks(queryTrackName, queryTrackArtist).observe(this, observer)

        this.similarInitialized = true
    }

    inner class SimilarTracksAdapter: RecyclerView.Adapter<SimilarTracksAdapter.SimilarTracksViewHolder>() {

        override fun getItemCount(): Int {
            return trackList.size
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SimilarTracksViewHolder {
            var itemView = LayoutInflater.from(p0.context).inflate(R.layout.similar_tracks_items,p0,false)
            return SimilarTracksViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: SimilarTracksViewHolder, p1: Int) {
            val track = trackList[p1]
            val trackImage = track.getImages()
            if(trackImage.size == 0){

            }else{
                var imageIndex = min(3,trackImage.size - 1)
                Picasso.with(this@FragmentSimilarTracks.parentContext).load(trackImage[imageIndex]).into(p0.trackImg)
            }
            p0.trackTitle.text = track.getName()

            p0.row.setOnClickListener {
                val intent = Intent(this@FragmentSimilarTracks.parentContext, TrackDetailActivity::class.java)
                intent.putExtra("Track", track)
                startActivity(intent)
            }
        }



        inner class SimilarTracksViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val row = itemView

            var trackImg: ImageView = itemView.similar_track_cover
            var trackTitle: TextView = itemView.similar_track_title
//            var trackArtist: TextView = itemView.product_price
        }
    }
}