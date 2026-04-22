package com.studiocar.studio

import android.app.Application
import timber.log.Timber

class StudioCarApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Global Exception Handler
        val originalHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Timber.e(throwable, "FATAL CRASH in thread ${thread.name}")
            originalHandler?.uncaughtException(thread, throwable)
        }

        com.studiocar.studio.ui.viewmodels.ContextHolder.context = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
