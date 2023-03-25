package ie.healthylunch.app.data.model.parentDetailsModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data(@SerializedName("phone")
                val phone: String = "",
                @SerializedName("l_name")
                val lName: String = "",
                @SerializedName("f_name")
                val fName: String = "",
                @SerializedName("kitchen_name")
                val kitchenName: String = "",
                @SerializedName("wallet_balance")
                val walletBalance: String = "",
                @SerializedName("display_name")
                val displayName: String = "",
                @SerializedName("email")
                val email: String = "",
                @SerializedName("username")
                val username: String = "") : Parcelable