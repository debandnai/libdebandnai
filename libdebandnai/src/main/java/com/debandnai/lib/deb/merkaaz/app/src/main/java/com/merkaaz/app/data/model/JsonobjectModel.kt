package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

data class JsonobjectModel(
    @SerializedName("response")
    var response: ResponseModel? = ResponseModel()
)
