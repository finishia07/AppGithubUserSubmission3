package com.finishia.consumerapp.activity

import android.content.Intent
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.finishia.consumerapp.DatabaseContract.FavColumns.Companion.CONTENT_URI
import com.finishia.consumerapp.Gits
import com.finishia.consumerapp.MappingHelper
import com.finishia.consumerapp.R
import com.finishia.consumerapp.adapter.FavoriteAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: FavoriteAdapter
    private var title: String = "Halaman Favorite"

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setActionBarTitle(title)
        supportActionBar!!.title = "Halaman Favorite User"

        rv_favorite.layoutManager= LinearLayoutManager(this)
        rv_favorite.setHasFixedSize(true)

        adapter = FavoriteAdapter(this)
        rv_favorite.adapter= adapter
        adapter.notifyDataSetChanged()

        adapter.setOnClickCallback(object : FavoriteAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Gits){
                showSelectedUser(data)
            }
        })

        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        val myObserver = object : ContentObserver(handler){
            override fun onChange(self: Boolean) {
                loadFavAsync()
            }
        }

        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)

        if (savedInstanceState == null) {
            loadFavAsync()
        } else {
            savedInstanceState.getParcelableArrayList<Gits>(EXTRA_STATE)?.also { adapter.listGits = it }
        }
    }

    private fun showSelectedUser(data: Gits) {
        val intent= Intent(this@MainActivity, DetailGithubActivity::class.java)
        intent.putExtra(DetailGithubActivity.EXTRA_GITS, data)
        startActivity(intent)
    }

    private fun loadFavAsync() {
        GlobalScope.launch(Dispatchers.Main){
            progressBarFav.visibility= View.VISIBLE
            val deferredGits = async(Dispatchers.IO){
                val cursor = contentResolver.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }

            progressBarFav.visibility = View.INVISIBLE
            val gits = deferredGits.await()
            if (gits.size> 0) {
                adapter.listGits = gits
            } else {
                adapter.listGits = ArrayList()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listGits)
    }

    private fun setActionBarTitle(title: String?) {
        supportActionBar?.title = title
    }
}