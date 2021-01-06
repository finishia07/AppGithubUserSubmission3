package com.finishia.appgithubusersubmission3.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.finishia.appgithubusersubmission3.BuildConfig.GITHUB_TOKEN
import com.finishia.appgithubusersubmission3.R
import com.finishia.appgithubusersubmission3.adapter.ListGitsAdapter
import com.finishia.appgithubusersubmission3.entity.Gits
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_detail_github.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    var username: String? = null
    private var list:ArrayList<Gits> = arrayListOf()
    private var title: String = "Github User App"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setActionBarTitle(title)

        getGithubUser()
        showRecyclerList()
    }

    private fun getGithubUser() {
        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/sidiqpermana"
        client.addHeader("Authorization", GITHUB_TOKEN)
        client.addHeader("User-Agent", "request")
        progressBarMain.visibility= View.VISIBLE

        client.get(url,  object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>, responseBody: ByteArray) {

                //Jika koneksi berhasil
                progressBarMain.visibility = View.INVISIBLE

                val listGithub = ArrayList<Gits>()
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject= JSONObject(result)

                    val id = responseObject.getString("id")
                    val login= responseObject.getString("login")
                    val photo= responseObject.getString("avatar_url")
                    val user= responseObject.getString("name")
                    val follower= responseObject.getString("followers")
                    val following= responseObject.getString("following")
                    val repository= responseObject.getString("public_repos")
                    val company= responseObject.getString("company")
                    val location= responseObject.getString("location")

                    val github = Gits()
                    github.id   = id
                    github.username = login
                    github.avatar = photo
                    github.name= user
                    github.follower = follower
                    github.following = following
                    github.repository = repository
                    github.company = company
                    github.location= location
                    listGithub.add(github)

                    //set data ke adapter
                    val listGitsAdapter = ListGitsAdapter(listGithub)
                    rv_gits.adapter = listGitsAdapter

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable) {

                //Jika koneksi gagal
                progressBarMain.visibility = View.INVISIBLE

                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            //Methode ketika search selesai
            override fun onQueryTextSubmit(query: String): Boolean {
                Toast.makeText(this@MainActivity, query, Toast.LENGTH_SHORT).show()
                getDataUserFromApi(query)
                return true
            }

            //Methode untuk merespon tiap perubahan huruf pada searchView
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return true
    }

    private fun getDataUserFromApi(username: String) {
        progressBarMain.visibility= View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://api.github.com/search/users?q=$username"
        client.addHeader("Authorization", GITHUB_TOKEN)
        client.addHeader("User-Agent", "request")

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {

                //Jika koneksi berhasil
                progressBarMain.visibility = View.INVISIBLE

                val listGithub = ArrayList<Gits>()
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    //parsing JSON
                    val responseObject = JSONObject(result)
                    val items = responseObject.getJSONArray("items")

                    for (i in 0 until items.length()) {
                        val item = items.getJSONObject(i)
                        val login = item.getString("login")
                        val photo = item.getString("avatar_url")
                        val id = item.getString("id")

                        val github = Gits()
                        github.username = login
                        github.avatar = photo
                        github.id = id
                        listGithub.add(github)

                        //set data ke adapter
                        val listGitAdapter = ListGitsAdapter(listGithub, )
                        rv_gits.adapter = listGitAdapter
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable) {

                //Jika koneksi gagal
                progressBarMain.visibility = View.INVISIBLE

                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showRecyclerList() {
        rv_gits.layoutManager = LinearLayoutManager(this)
        rv_gits.setHasFixedSize(true)

        val listGitsAdapter = ListGitsAdapter(list, )
        rv_gits.adapter = listGitsAdapter

        listGitsAdapter.setOnItemClickCallback(object : ListGitsAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Gits){
                val detailGithubIntent = Intent(this@MainActivity, DetailGithubActivity::class.java)
                detailGithubIntent.putExtra(DetailGithubActivity.EXTRA_GITS, data)
                showSelectedUser(data)
                startActivity(detailGithubIntent)
            }
        })
    }

    private fun showSelectedUser(user: Gits){
        Toast.makeText(this, "Kamu memilih "+ user.username, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setMode(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun setMode(selectedMode: Int){
        when (selectedMode){
            R.id.action_settings ->{
                val setIntent = Intent(this@MainActivity, SettingActivity::class.java)
                startActivity(setIntent)
            }

            R.id.action_favorit ->{
                val favIntent= Intent(this@MainActivity, FavoriteGithubActivity::class.java)
                startActivity(favIntent)
            }
        }
    }

    private fun setActionBarTitle(title: String){
        supportActionBar?.title = title
    }
}