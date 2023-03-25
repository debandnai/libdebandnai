package com.merkaaz.app.ui.base

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MarkrezAplication :Application(), OnMapsSdkInitializedCallback {
    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST, this)
    }

    override fun onMapsSdkInitialized(p0: MapsInitializer.Renderer) {

    }



}