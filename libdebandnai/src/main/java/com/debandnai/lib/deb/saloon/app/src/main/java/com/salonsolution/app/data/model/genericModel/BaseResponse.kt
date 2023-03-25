package com.salonsolution.app.data.model.genericModel

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("response") var response: Response<T>? = Response()
)
