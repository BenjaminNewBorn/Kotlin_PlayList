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
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.playlist.R
import com.example.playlist.activity.TrackDetailActivity
import com.example.playlist.model.Track
import com.example.playlist.viewmodel.TrackViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.add_tracks_content_main.*
import kotlinx.android.synthetic.main.add_tracks_item.view.*
import kotlinx.android.synthetic.main.fragment_add_track_list.*
import kotlinx.android.synthetic.main.fragment_result_list.*
import kotlinx.android.synthetic.main.result_list_item.view.*
import kotlin.math.min

@SuppressLint("ValidFragment")
class ResultListFragment(context: Context, query:String):Fragment() {
    private var adapter = ResultAdapter()
    private var listener: ResultListFragment.OnFragmentInteractionListener? = null
    private lateinit var viewModel: TrackViewModel
    private var parentContext:Context = context
    private var listInitialized = false


    private var queryString: String = query
    private var trackList: ArrayList<Track> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_result_list, container, false)
    }

    override fun onStart() {
        super.onStart()

        search_artist_result.layoutManager = GridLayoutManager(this.context, 2)
        search_artist_result.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        viewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)

        val observer = Observer<ArrayList<Track>> {
            search_artist_result.adapter = adapter
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
        viewModel.searchTrackFromArtist(queryString).observe(this,observer)

        this.listInitialized = true

    }

    inner class ResultAdapter: RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

        override fun getItemCount(): Int {
            return trackList.size
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ResultViewHolder {
            var itemView = LayoutInflater.from(p0.context).inflate(R.layout.result_list_item,p0,false)
            return ResultViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: ResultViewHolder, p1: Int) {
            var track = trackList[p1]
            var trackImage = track.getImages()
            if(trackImage.size == 0){

            }else{
                var imageIndex = min(3,trackImage.size - 1)
                Picasso.with(this@ResultListFragment.context).load(trackImage[2]).into(p0.trackImg)
            }
            p0.trackTitle.text = track.getName()
            p0.row.setOnClickListener {
                val intent = Intent(this@ResultListFragment.parentContext, TrackDetailActivity::class.java)
                intent.putExtra("Track", track)
                startActivity(intent)
            }
        }

        inner class ResultViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            var row = itemView

            var trackImg: ImageView = itemView.track_result_cover_img
            var trackTitle: TextView = itemView.track_result_title_tv
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