package com.example.weatherapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import java.io.InputStreamReader

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().remove("None").apply()

        minput = InputStreamReader(assets.open("RussianTowns.csv"))
    }

    companion object {
        lateinit var prefs: SharedPreferences
        lateinit var minput: InputStreamReader
    }
}