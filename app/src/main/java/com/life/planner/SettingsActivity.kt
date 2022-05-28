package com.life.planner

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import android.app.AlertDialog
import android.widget.TextView
import android.widget.Toast


@Suppress("DEPRECATION")
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = resources.getString(R.string.action_settings)
        setContentView(R.layout.settings_main)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<TextView>(R.id.settingLanguage).setOnClickListener {
            dialogChooseLanguage()
        }
        findViewById<TextView>(R.id.settingAbout).setOnClickListener {
            Toast.makeText(this, resources.getString(R.string.settings_about_toast), Toast.LENGTH_LONG).show()
        }
    }

    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        Toast.makeText(this, resources.getString(R.string.settings_chooseLanguage_toast), Toast.LENGTH_SHORT).show()
    }

    private fun dialogChooseLanguage() {
        val languageDialog = AlertDialog.Builder(this)
        languageDialog.setTitle(resources.getString(R.string.settings_chooseLanguage_dialog))
        val languageList = arrayOf(resources.getString(R.string.settings_chooseLanguage_polish), resources.getString(R.string.settings_chooseLanguage_English))
        languageDialog.setItems(languageList) { _, which ->
            when (which) {
                0 -> { setLocale("pl") }
                1 -> { setLocale("en") }
            }
        }
        languageDialog.create().show()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}