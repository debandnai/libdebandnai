package ie.healthylunch.app.data.model.transactionListModel

import com.google.gson.annotations.SerializedName

data class TransactionListResponse(@SerializedName("response")
                                   val response: Response
)