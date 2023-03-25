package com.merkaaz.app.data.genericmodel

import com.google.gson.annotations.SerializedName
import com.merkaaz.app.data.model.PublishModel
import com.merkaaz.app.data.model.StatusModel

data class ResponseGenericModel <T>(
    @SerializedName("data")
    var data: T? = null,
    @SerializedName("status")
    var status: StatusModel? = StatusModel(),

    @SerializedName("publish")
    var publish: PublishModel? = PublishModel()
)