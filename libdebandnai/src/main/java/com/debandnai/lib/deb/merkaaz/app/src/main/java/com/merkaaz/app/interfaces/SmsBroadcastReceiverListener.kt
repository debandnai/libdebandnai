package com.merkaaz.app.interfaces

import android.content.Intent




interface SmsBroadcastReceiverListener {
    fun onSuccess(intent: Intent?)

    fun onFailure()
}