package com.finishia.appgithubusersubmission3.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.finishia.appgithubusersubmission3.R
import com.finishia.appgithubusersubmission3.fragment.PreferencesFragment

class SettingActivity : AppCompatActivity() {

    private var title: String = "Halaman Setting"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setActionBarTitle(title)

        supportActionBar!!.title = "Halaman Setting"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.beginTransaction().add(R.id.setting_holder, PreferencesFragment()).commit()
    }

    private fun setActionBarTitle (title: String){
        supportActionBar?.title = title
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}