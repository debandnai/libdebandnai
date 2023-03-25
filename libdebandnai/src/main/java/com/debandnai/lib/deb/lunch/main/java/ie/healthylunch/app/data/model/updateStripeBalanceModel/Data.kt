package ie.healthylunch.app.data.model.updateStripeBalanceModel

import com.google.gson.annotations.SerializedName

data class Data(@SerializedName("wallet_balance")
                val walletBalance: Double = 0.0)