package ie.healthylunch.app.data.model.transactionListModel

import com.google.gson.annotations.SerializedName
import ie.healthylunch.app.utils.MethodClass

data class DataItem(
    @SerializedName("transaction_id")
    val transactionId: String = "",
    @SerializedName("wallettransuctionid")
    val wallettransuctionid: Int = 0,
    @SerializedName("student_lastname")
    val studentLastname: String = "",
    @SerializedName("template_name")
    val templateName: String? = "",
    @SerializedName("student_firstname")
    val studentFirstname: String = "",
    @SerializedName("transaction_for")
    val transactionFor: String = "",
    @SerializedName("parent_id")
    val parentId: String = "",
    @SerializedName("transaction_amount")
    val transactionAmount: String = "",
    @SerializedName("student_id")
    val studentId: Int = 0,
    @SerializedName("transaction_time")
    val transactionTime: String = "",
    @SerializedName("transaction_type")
    val transactionType: String = "",
    @SerializedName("products")
    val products: Any? = null
)