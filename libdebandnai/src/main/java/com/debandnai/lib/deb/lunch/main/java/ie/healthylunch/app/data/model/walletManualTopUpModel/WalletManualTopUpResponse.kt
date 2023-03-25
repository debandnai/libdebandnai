package ie.healthylunch.app.data.model.walletManualTopUpModel

import com.google.gson.annotations.SerializedName

data class WalletManualTopUpResponse(
    @SerializedName("response")
    val response: Response
)