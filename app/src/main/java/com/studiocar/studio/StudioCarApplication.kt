package com.studiocar.studio

import android.app.Application
import timber.log.Timber

@Suppress("unused")
class StudioCarApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        com.studiocar.studio.ui.viewmodels.ContextHolder.context = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
