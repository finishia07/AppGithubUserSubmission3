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
import kotlinx.android.synthetic.main.fragment_followers.*
import org.json.JSONArray

class FollowersFragment : Fragment() {

    var username: String? = null
    private lateinit var rvGits: RecyclerView
    private var listGits: ArrayList<Gits> = arrayListOf()

    companion object{
        private val TAG = FollowersFragment::class.java.simpleName
        const val ARG_USERNAME = "username"

        fun newInstance(username: String?): FollowersFragment {
            val fragment = FollowersFragment()
            val bundle = Bundle()
            bundle.putString(ARG_USERNAME, username)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_followers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvGits= rv_gits
        rv_gits.setHasFixedSize(true)

        val listFollowers= arguments?.getString(ARG_USERNAME)
        getFollowersList(listFollowers)
        recyclerList()
    }

    private fun recyclerList() {
        rvGits.layoutManager = LinearLayoutManager(activity)
        val listGitsAdapter = ListGitsAdapter(listGits)
        rvGits.adapter = listGitsAdapter
    }

    private fun getFollowersList(username: String?) {

        myProgressBar.visibility= View.VISIBLE

        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/$username/followers"
        client.addHeader("Authorization", GITHUB_TOKEN)
        client.addHeader("User-Agent", "request")
        myProgressBar.visibility= View.VISIBLE

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
                //Jika koneksi berhasil
                myProgressBar.visibility = View.INVISIBLE

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
                        rvGits.adapter= listGitsAdapter
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable) {

                //Jika koneksi gagal
                myProgressBar.visibility = View.INVISIBLE

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
}