package ie.healthylunch.app.data.model.transactionListPagingModel

import com.google.gson.annotations.SerializedName

data class TransactionListResponse(
    @SerializedName("response")
    val response: Response?
)

data class Response(@SerializedName("raws") var raws: Raws? = null)
data class Raws(
    @SerializedName("success_message") var successMessage: String? = null,
    @SerializedName("data") var data: Data? = null,
    @SerializedName("publish") var publish: Publish? = null
)

data class Publish(
    @SerializedName("version") var version: String? = null,
    @SerializedName("developer") var developer: String? = null
)

data class Data(
    @SerializedName("transaction_list") var transactionList: ArrayList<TransactionList>? = null,
    @SerializedName("total_count") var totalCount: Int = 0
)

data class TransactionList(
    @SerializedName("parent_id") var parentId: String? = "",
    @SerializedName("products") var products: Any? = null,
    @SerializedName("student_firstname") var studentFirstname: String? = "",
    @SerializedName("student_id") var studentId: Int = 0,
    @SerializedName("student_lastname") var studentLastname: String? = "",
    @SerializedName("template_name") var templateName: String? = "",
    @SerializedName("transaction_amount") var transactionAmount: String? = "",
    @SerializedName("transaction_for") var transactionFor: String? = "",
    @SerializedName("transaction_id") var transactionId: String? = "",
    @SerializedName("transaction_time") var transactionTime: String? = "",
    @SerializedName("transaction_type") var transactionType: String? = "",
    @SerializedName("wallettransuctionid") var wallettransuctionid: Int = 0
)
