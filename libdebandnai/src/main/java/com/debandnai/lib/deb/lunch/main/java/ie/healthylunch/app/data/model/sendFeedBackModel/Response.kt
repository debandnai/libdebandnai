package ie.healthylunch.app.data.model.sendFeedBackModel

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("raws")
    val raws: Raws
)