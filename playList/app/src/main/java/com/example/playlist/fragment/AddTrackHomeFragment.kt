package com.example.playlist.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.playlist.R
import kotlinx.android.synthetic.main.add_tracks_content_main.*

@SuppressLint("ValidFragment")
class AddTrackHomeFragment(context: Context):Fragment() {
    private  var parentContext = context
    private var initialized: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_tracks_content_main, container, false)
    }

    override fun onStart() {
        super.onStart()

        if(!this.initialized) {
            var fm = fragmentManager
            var ft = fm?.beginTransaction()
            ft?.add(R.id.list_holder, AddTracksForm(this.parentContext), "TOP_TRACK")
            ft?.commit()

            search_edit_text.setOnEditorActionListener { _, actionId, _ ->
                Log.d("actionId",actionId.toString())
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val searchText = search_edit_text.text
                    search_edit_text.setText("")

                    //hide soft keyboard
                    val inputManager:InputMethodManager = this.parentContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(this.view?.windowToken, InputMethodManager.SHOW_FORCED)

                    if (searchText.toString() == "") {
                        val toast = Toast.makeText(this.parentContext, "Please enter text", Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER,0,0)
                        toast.show()
                        return@setOnEditorActionListener true
                    }
                    else {
                        performSearch(searchText.toString())
                        return@setOnEditorActionListener false
                    }

                }
                return@setOnEditorActionListener false

            }

            this.initialized = true

        }
    }

    private fun performSearch(query: String) {
        val fm = fragmentManager

        val fragment = ResultListFragment(this.parentContext, query)
        val ft = fm?.beginTransaction()
        ft?.replace(R.id.list_holder, fragment, "RESULTS_FRAG")
        ft?.commit()
    }





}