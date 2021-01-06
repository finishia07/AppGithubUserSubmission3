package com.finishia.consumerapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finishia.consumerapp.BuildConfig.GITHUB_TOKEN
import com.finishia.consumerapp.Gits
import com.finishia.consumerapp.R
import com.finishia.consumerapp.adapter.ListGitsAdapter
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.fragment_following.*
import org.json.JSONArray
import kotlinx.android.synthetic.main.fragment_following.rv_gits as rv_gits1

class FollowingFragment : Fragment() {

    var username: String? = null
    private lateinit var rvGits: RecyclerView
    private var list: ArrayList<Gits> = arrayListOf()

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

    private fun getFollowingList(username: String?) {
        myProgressBar1.visibility = View.VISIBLE

        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/$username/following"
        client.addHeader("Authorization", GITHUB_TOKEN)
        client.addHeader("User-Agent", "request")

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {

                myProgressBar1.visibility = View.INVISIBLE

                val listGithub = ArrayList<Gits>()
                val result = String(responseBody)
                Log.d(TAG, result)
                try {

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_following, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvGits = rv_gits1
        rvGits.setHasFixedSize(true)

        val listFollowing = arguments?.getString(ARG_USERNAME)
        getFollowingList(listFollowing)
        recyclerList()
    }

    private fun recyclerList() {
        rvGits.layoutManager = LinearLayoutManager(activity)
        val listGitsAdapter = ListGitsAdapter(list)
        rvGits.adapter = listGitsAdapter
    }
}