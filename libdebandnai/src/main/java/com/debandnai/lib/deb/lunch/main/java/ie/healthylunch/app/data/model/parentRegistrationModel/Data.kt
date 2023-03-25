package ie.healthylunch.app.data.model.parentRegistrationModel

data class Data(
    val display_name: String,
    val email: String,
    val expire_at: Int,
    val id: Int,
    val is_active: Int,
    val is_addcard: Int,
    val os_update: Int,
    val phone: String,
    val ref_user_id: Int,
    val refresh_token: String,
    val studentcount: Int,
    val token: String,
    val user_type: String,
    val username: String
)