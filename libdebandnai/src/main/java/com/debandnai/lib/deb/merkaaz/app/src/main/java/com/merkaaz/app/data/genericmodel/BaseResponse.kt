package com.merkaaz.app.data.genericmodel

import com.google.gson.annotations.SerializedName
import com.merkaaz.app.data.model.ResponseModel

data class BaseResponse <T>(
    @SerializedName("response")
    var response: ResponseModel? = ResponseModel()
)