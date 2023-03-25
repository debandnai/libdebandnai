package com.merkaaz.app.utils

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import com.merkaaz.app.interfaces.UserApprovedCallBack
import com.merkaaz.app.utils.Constants.APPROVED_USER
import java.util.*

class UserApprovedBroadcastReceiver : BroadcastReceiver(){
    companion object {
        var listener: UserApprovedCallBack? = null
    }
    override fun onReceive(context: Context?, intent: Intent?) {
            listener?.userApprovedStatus()

        Log.d(TAG, "onReceive: test ")
    }

}