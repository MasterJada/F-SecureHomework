package dev.jetlaunch.locationtracker

import android.app.Application
import dev.jetlaunch.locationtracker.locator.ServiceLocator

class App: Application() {
    override fun onCreate() {
        super.onCreate()
      ServiceLocator.init(this)
    }
}