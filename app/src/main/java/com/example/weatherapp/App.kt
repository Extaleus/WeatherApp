package com.example.weatherapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().remove("None").apply()
    }

    companion object {
        lateinit var prefs: SharedPreferences
    }
}