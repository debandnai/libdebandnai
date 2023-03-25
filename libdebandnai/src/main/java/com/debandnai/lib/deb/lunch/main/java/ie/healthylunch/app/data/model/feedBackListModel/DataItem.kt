package ie.healthylunch.app.data.model.feedBackListModel

import com.google.gson.annotations.SerializedName

data class DataItem(@SerializedName("parent_id")
                    val parentId: Int = 0,
                    @SerializedName("l_name")
                    val lName: String = "",
                    @SerializedName("f_name")
                    val fName: String = "",
                    @SerializedName("message_id")
                    val messageId: Int = 0,
                    @SerializedName("message")
                    val message: String = "",
                    @SerializedName("added_on")
                    val addedOn: String = "")