package com.example.playlist.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DividerItemDecoration
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
import kotlinx.android.synthetic.main.add_tracks_content_main.view.*
import kotlinx.android.synthetic.main.fragment_play_list.*
import kotlinx.android.synthetic.main.fragment_result_list.*
import kotlinx.android.synthetic.main.play_list_item.view.*
import kotlinx.android.synthetic.main.result_list_item.view.*
import java.util.*
import kotlin.math.min

@SuppressLint("ValidFragment")
class ViewTracks(context: Context):Fragment() {
    private var adapter = PlayListAdapter()
    private var parentContext: Context = context
    private lateinit var viewModel: TrackViewModel
    private var listener: ViewTracks.OnFragmentInteractionListener? = null

    private var trackList: ArrayList<Track> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_play_list, container, false)
    }

    override fun onStart() {
        super.onStart()

        play_track_list.layoutManager = LinearLayoutManager(parentContext)
        play_track_list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        viewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)

        val observer = Observer<ArrayList<Track>> {
            play_track_list.adapter = adapter
            var result = DiffUtil.calculateDiff(object: DiffUtil.Callback(){
                override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
                    if(p0 < trackList.size && p1 < trackList.size) {
                        return trackList[p0].getName() == trackList[p1].getName()
                    }
                    return false
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
        viewModel.getFavorites().observe(this,observer)
    }

    inner class PlayListAdapter: RecyclerView.Adapter<PlayListAdapter.PlayListViewHolder>() {

        override fun getItemCount(): Int {
            return trackList.size
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PlayListViewHolder {
            var itemView = LayoutInflater.from(p0.context).inflate(R.layout.play_list_item,p0,false)
            return PlayListViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: PlayListViewHolder, p1: Int) {
            var track = trackList[p1]

            p0.trackTitle.text = track.getName()
            p0.trackArtist.text = track.getArtist()
            p0.row.setOnClickListener {
                val intent = Intent(this@ViewTracks.parentContext, TrackDetailActivity::class.java)
                intent.putExtra("Track", track)
                startActivity(intent)
            }
        }

        inner class PlayListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            var row = itemView

            var trackArtist: TextView = itemView.play_list_artist_tv
            var trackTitle: TextView = itemView.play_list_title_tv
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }
}