package com.finishia.appgithubusersubmission3.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finishia.appgithubusersubmission3.BuildConfig.GITHUB_TOKEN
import com.finishia.appgithubusersubmission3.R
import com.finishia.appgithubusersubmission3.adapter.ListGitsAdapter
import com.finishia.appgithubusersubmission3.entity.Gits
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.fragment_following.*
import org.json.JSONArray

class FollowingFragment : Fragment() {

    var username: String? = null
    private var listGits: ArrayList<Gits> = arrayListOf()
    private lateinit var rvGits: RecyclerView

    companion object {
        private val TAG = FollowingFragment::class.java.simpleName
        const val ARG_USERNAME = "username"

        fun newInstance(username: String?): FollowingFragment {
            val fragment = FollowingFragment()
            val bundle = Bundle()
            bundle.putString(ARG_USERNAME, username)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_following, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvGits= rv_gits
        rvGits.setHasFixedSize(true)

        val listFollowing = arguments?.getString(ARG_USERNAME)
        getFollowingList(listFollowing)
        recyclerList()
    }

    private fun getFollowingList(username: String?) {

        myProgressBar1.visibility = View.VISIBLE

        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/$username/following"
        client.addHeader("Authorization", GITHUB_TOKEN)
        client.addHeader("User-Agent", "request")

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {

                //Jika koneksi berhasil
                myProgressBar1.visibility = View.INVISIBLE

                val listGithub = ArrayList<Gits>()
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    //parsing JSON
                    val jsonArray = JSONArray(result)

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val login = item.getString("login")
                        val photo = item.getString("avatar_url")

                        val github = Gits()
                        github.username = login
                        github.avatar = photo
                        listGithub.add(github)

                        //set adapter
                        val listGitsAdapter = ListGitsAdapter(listGithub)
                        rvGits.adapter = listGitsAdapter
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable) {

                //Jika koneksi gagal
                myProgressBar1.visibility = View.INVISIBLE

                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Log.d("Exception", errorMessage)
            }
        })
    }

    private fun recyclerList() {
        rvGits.layoutManager = LinearLayoutManager(activity)
        val listGitsAdapter = ListGitsAdapter(listGits)
        rvGits.adapter = listGitsAdapter
    }
}