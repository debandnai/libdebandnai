package com.salonsolution.app.data.model.genericModel

import com.google.gson.annotations.SerializedName

data class Response<T>(
    @SerializedName("data") var data: T? = null,
    @SerializedName("status") var status: Status? = Status(),
    @SerializedName("publish") var publish: Publish? = Publish()

)



