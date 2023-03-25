package ie.healthylunch.app.data.model.refreshTokenModel

import com.google.gson.annotations.SerializedName

data class Data(@SerializedName("is_addcard")
                val isAddcard: Int = 0,
                @SerializedName("ref_user_id")
                val refUserId: Int = 0,
                @SerializedName("is_active")
                val isActive: Int = 0,
                @SerializedName("studentcount")
                val studentcount: Int = 0,
                @SerializedName("os_update")
                val osUpdate: Int = 0,
                @SerializedName("display_name")
                val displayName: String = "",
                @SerializedName("token")
                val token: String = "",
                @SerializedName("refresh_token")
                val refreshToken: String = "",
                @SerializedName("user_type")
                val userType: String = "",
                @SerializedName("phone")
                val phone: String = "",
                @SerializedName("expire_at")
                val expireAt: Int = 0,
                @SerializedName("id")
                val id: Int = 0,
                @SerializedName("email")
                val email: String = "",
                @SerializedName("username")
                val username: String = "")