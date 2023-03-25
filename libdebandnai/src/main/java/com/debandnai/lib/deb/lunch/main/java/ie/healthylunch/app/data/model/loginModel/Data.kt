package ie.healthylunch.app.data.model.loginModel

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("is_addcard")
    val isAddcard: Int? = null,
    @SerializedName("ref_user_id")
    val refUserId: Int? = null,
    @SerializedName("is_active")
    val isActive: Int? = null,
    @SerializedName("studentcount")
    val studentcount: Int? = null,
    @SerializedName("os_update")
    val osUpdate: Int? = null,
    @SerializedName("display_name")
    var displayName: String? = null,
    @SerializedName("token")
    var token: String? = null,
    @SerializedName("refresh_token")
    var refreshToken: String? = null,
    @SerializedName("user_type")
    val userType: String? = null,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("expire_at")
    val expireAt: Int? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("username")
    val username: String? = null,
    @SerializedName("School_details")
    val schoolDetails: List<SchoolDetailsItem>?
)