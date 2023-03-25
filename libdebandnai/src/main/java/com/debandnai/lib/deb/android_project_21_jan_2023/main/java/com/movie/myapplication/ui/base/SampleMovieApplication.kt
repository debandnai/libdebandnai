package com.movie.myapplication.ui.base

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SampleMovieApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
    }
}