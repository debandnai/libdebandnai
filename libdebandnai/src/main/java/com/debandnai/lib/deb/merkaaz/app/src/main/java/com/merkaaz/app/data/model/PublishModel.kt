package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

data class PublishModel(
    @SerializedName("version")
    var version   : String? = null,

    @SerializedName("developer")
    var developer : String? = null

)
