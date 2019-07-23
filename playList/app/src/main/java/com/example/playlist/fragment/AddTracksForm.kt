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
import kotlin.math.min

@SuppressLint("ValidFragment")
class AddTracksForm(context:Context): Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var viewModel: TrackViewModel
    private var adapter = AddTracksAdapter()
    private var parentContext:Context = context
    private var listInitialized = false

    private var trackList:ArrayList<Track> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_track_list,container,false)
    }

    override fun onStart() {
        super.onStart()

        add_track_results.layoutManager = GridLayoutManager(this.context, 2)
        add_track_results.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
        viewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)

        val observer = Observer<ArrayList<Track>> {
            add_track_results.adapter = adapter
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

        viewModel.getTopTracks().observe(this, observer)

        this.listInitialized = true
    }

    inner class AddTracksAdapter:RecyclerView.Adapter<AddTracksAdapter.AddTracksViewHolder>() {

        override fun getItemCount(): Int {
            return trackList.size
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AddTracksViewHolder {
            var itemView = LayoutInflater.from(p0.context).inflate(R.layout.add_tracks_item,p0,false)
            return AddTracksViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: AddTracksViewHolder, p1: Int) {
            val track = trackList[p1]
            val trackImage = track.getImages()
            if(trackImage.size == 0){

            }else{
                var imageIndex = min(3,trackImage.size - 1)
                Picasso.with(this@AddTracksForm.context).load(trackImage[2]).into(p0.trackImg)
            }
            p0.trackTitle.text = track.getName()

            p0.row.setOnClickListener {
                val intent = Intent(this@AddTracksForm.parentContext, TrackDetailActivity::class.java)
                intent.putExtra("Track", track)
                startActivity(intent)
            }
        }



        inner class AddTracksViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val row = itemView

            var trackImg: ImageView = itemView.track_cover_img
            var trackTitle: TextView = itemView.track_title_tv
//            var trackArtist: TextView = itemView.product_price
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