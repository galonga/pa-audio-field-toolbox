package de.minmon.app

import android.app.Application
import de.minmon.design.utils.Preferences

class  App : Application() {
    override fun onCreate() {
        super.onCreate()

        Preferences.init(this)
    }
}
