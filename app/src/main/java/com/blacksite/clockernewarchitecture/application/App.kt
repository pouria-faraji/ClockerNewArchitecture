package com.blacksite.clockernewarchitecture.application

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import java.util.*

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        changeSystemLocaleToEN()
        App.appContext = applicationContext
    }
    private fun changeSystemLocaleToEN() {
        val languageToLoad = "en"
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    companion object {
        var test = false
        var appContext: Context? = null
            private set
    }
}