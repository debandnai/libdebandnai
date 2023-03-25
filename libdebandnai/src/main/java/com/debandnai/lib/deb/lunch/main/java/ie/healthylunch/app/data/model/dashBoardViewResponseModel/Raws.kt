package ie.healthylunch.app.data.model.dashBoardViewResponseModel

import com.google.gson.annotations.SerializedName
import ie.healthylunch.app.data.model.baseModel.Publish

data class Raws(
    @SerializedName("data") val data: Data? =null,
    @SerializedName("success_message") val successMessage: String = "",
    @SerializedName("publish") val publish: Publish?=null
)