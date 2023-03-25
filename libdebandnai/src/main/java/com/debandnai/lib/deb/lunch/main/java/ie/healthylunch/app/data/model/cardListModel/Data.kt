package ie.healthylunch.app.data.model.cardListModel

import com.google.gson.annotations.SerializedName

data class Data(@SerializedName("wallet_balance")
                val walletBalance: String = "",
                @SerializedName("extra_student_top_up_amount")
                val extraStudentTopUpAmount: Int = 0,
                @SerializedName("transuction")
                val transuction: String = "",
                @SerializedName("cardnumber")
                val cardnumber: String = "",
                @SerializedName("top_up_amount")
                val topUpAmount: String = "",
                @SerializedName("brand")
                val brand: String = "",
                @SerializedName("threshold_wallet_amount")
                val thresholdWalletAmount: Int = 0)