package com.finishia.consumerapp.activity

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.finishia.consumerapp.BuildConfig.GITHUB_TOKEN
import com.finishia.consumerapp.DatabaseContract
import com.finishia.consumerapp.DatabaseContract.FavColumns.Companion.CONTENT_URI
import com.finishia.consumerapp.Gits
import com.finishia.consumerapp.MappingHelper
import com.finishia.consumerapp.R
import com.finishia.consumerapp.adapter.SectionPagerAdapter
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_detail_github.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_row_gits.img_item_photo
import kotlinx.android.synthetic.main.item_row_gits.tv_item_company
import kotlinx.android.synthetic.main.item_row_gits.tv_item_follower
import kotlinx.android.synthetic.main.item_row_gits.tv_item_following
import kotlinx.android.synthetic.main.item_row_gits.tv_item_location
import kotlinx.android.synthetic.main.item_row_gits.tv_item_name
import kotlinx.android.synthetic.main.item_row_gits.tv_item_repository
import kotlinx.android.synthetic.main.item_row_gits.tv_item_userName
import org.json.JSONObject

class DetailGithubActivity: AppCompatActivity() {

    private var title: String = "Halaman Detail"
    private var statusFavorite = false
    private lateinit var uriWithid: Uri
    private var gits: Gits? = null

    companion object{
        private val TAG = DetailGithubActivity::class.java.simpleName
        const val EXTRA_GITS = "extra_gits"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_github)
        setActionBarTitle(title)

        supportActionBar!!.title = "Detail User"
        supportActionBar?.elevation= 0f

        var github = intent.getParcelableExtra<Gits>(EXTRA_GITS) as Gits

        Glide.with(this)
            .load(github.avatar)
            .apply(RequestOptions())
            .into(img_item_photo)

        val sectionPagerAdapter = SectionPagerAdapter(this, supportFragmentManager)
        sectionPagerAdapter.username= github.username
        view_pager.adapter= sectionPagerAdapter
        tabs.setupWithViewPager(view_pager)

        // Check user sudah ada di db
        uriWithid= Uri.parse(CONTENT_URI.toString() + "/" + github.id)
        val isFav = contentResolver.query(uriWithid, null, null, null, null)

        if (isFav !=null && isFav.moveToFirst()){
            github = MappingHelper.mapCursorToObject(isFav)
            isFav.close()
            statusFavorite = true
            setStatusFavorite(statusFavorite)
        }
        getDetailUser(github.username)
    }

    private fun getDetailUser(username: String?) {
        progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/$username"
        client.addHeader("Authorization", GITHUB_TOKEN)
        client.addHeader("User-Agent", "request")

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>, responseBody: ByteArray) {

                //Jika koneksi berhasil
                progressBar.visibility = View.INVISIBLE
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)

                    val id = responseObject.getString("id")
                    val login = responseObject.getString("login")
                    val photo = responseObject.getString("avatar_url")
                    val user = responseObject.getString("name")
                    val follower = responseObject.getString("followers")
                    val following = responseObject.getString("following")
                    val repository = responseObject.getString("public_repos")
                    val company = responseObject.getString("company")
                    val location = responseObject.getString("location")

                    val github = Gits()
                    github.id = id
                    github.username = login
                    github.avatar = photo
                    github.name = user
                    github.follower = follower
                    github.following = following
                    github.repository = repository
                    github.company = company
                    github.location = location

                    tv_item_userName.text = github.username.toString()
                    tv_item_name.text = github.name.toString()
                    tv_item_follower.text  = "Followers("+github.follower.toString()+")"
                    tv_item_following.text  = "Following("+github.following.toString()+")"
                    tv_item_repository.text = "Repositories("+github.repository.toString()+")"
                    tv_item_company.text = github.company.toString()
                    tv_item_location.text = github.location.toString()

                    fab.setOnClickListener {
                        if (!statusFavorite) {
                            val intent = Intent()
                            intent.putExtra(EXTRA_GITS, gits)

                            //insert user ke database
                            val values = ContentValues()
                            values.put(DatabaseContract.FavColumns._ID, github.id)
                            values.put(DatabaseContract.FavColumns.AVATAR, github.avatar)
                            values.put(DatabaseContract.FavColumns.USERNAME, github.username)
                            values.put(DatabaseContract.FavColumns.NAME, github.name)
                            values.put(DatabaseContract.FavColumns.REPOSITORY, github.repository)
                            values.put(DatabaseContract.FavColumns.FOLLOWER, github.follower)
                            values.put(DatabaseContract.FavColumns.FOLLOWING, github.following)
                            values.put(DatabaseContract.FavColumns.COMPANY, github.company)
                            values.put(DatabaseContract.FavColumns.LOCATION, github.location)

                            contentResolver.insert(CONTENT_URI, values)
                            statusFavorite = !statusFavorite
                            setStatusFavorite(statusFavorite)
                            Toast.makeText(this@DetailGithubActivity, "User ditambahkan ke favorite", Toast.LENGTH_SHORT).show()
                        } else {
                            // Delete user jika sudah ada di db
                            contentResolver.delete(uriWithid, null, null)
                            statusFavorite = !statusFavorite
                            setStatusFavorite(statusFavorite)
                            Toast.makeText( this@DetailGithubActivity, "User dihapus dari favorite", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable) {
                //Jika koneksi gagal
                progressBar.visibility = View.INVISIBLE

                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@DetailGithubActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setStatusFavorite(statusFavorite: Boolean) {
        if (statusFavorite) {
            fab.setImageResource(R.drawable.baseline_favorite)
        }
        else{
            fab.setImageResource(R.drawable.baseline_unfavorite)
        }
    }

    private fun setActionBarTitle (title: String){
        supportActionBar?.title = title
    }
}