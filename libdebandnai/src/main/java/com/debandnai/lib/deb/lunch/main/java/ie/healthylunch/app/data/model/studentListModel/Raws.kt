package ie.healthylunch.app.data.model.studentListModel

import com.google.gson.annotations.SerializedName
import ie.healthylunch.app.data.model.baseModel.Publish

data class Raws(@SerializedName("data")
                val data: List<DataItem> = ArrayList(),
                @SerializedName("success_message")
                val successMessage: String = "",
                @SerializedName("publish")
                val publish: Publish)