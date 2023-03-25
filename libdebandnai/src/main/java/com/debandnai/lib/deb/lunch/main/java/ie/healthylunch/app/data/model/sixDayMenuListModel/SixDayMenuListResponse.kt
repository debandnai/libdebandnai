package ie.healthylunch.app.data.model.sixDayMenuListModel

import com.google.gson.annotations.SerializedName

data class SixDayMenuListResponse(
    @SerializedName("response")
    val response: Response
)