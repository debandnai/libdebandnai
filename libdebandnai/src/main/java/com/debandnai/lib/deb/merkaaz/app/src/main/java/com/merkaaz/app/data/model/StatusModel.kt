package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

data class StatusModel(

    @SerializedName("msg")
    var msg : String?  = null,

    @SerializedName("action_status" )
    var actionStatus : Boolean? = null
)
