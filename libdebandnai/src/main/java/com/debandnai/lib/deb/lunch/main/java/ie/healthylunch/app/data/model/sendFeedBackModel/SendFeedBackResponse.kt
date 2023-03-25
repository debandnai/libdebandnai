package ie.healthylunch.app.data.model.sendFeedBackModel

import com.google.gson.annotations.SerializedName

data class SendFeedBackResponse(
    @SerializedName("response")
    val response: Response
)