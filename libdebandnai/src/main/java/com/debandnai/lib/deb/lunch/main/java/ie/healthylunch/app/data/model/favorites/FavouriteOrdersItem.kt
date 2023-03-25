package ie.healthylunch.app.data.model.favorites

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FavouriteOrdersItem(
    @SerializedName("fk_student_id")
    val fkStudentId: Int? = 0,
    @SerializedName("fk_sublunch_menu_id")
    val fkSublunchMenuId: Int? = 0,
    @SerializedName("user_type")
    val userType: String? = null,
    @SerializedName("fk_menu_id")
    val fkMenuId: Int? = 0,
    @SerializedName("all_id")
    val allId: List<AllIdItem?>? = ArrayList(),
    @SerializedName("week_day")
    val weekDay: Int? = 0,
    @SerializedName("order_price")
    val orderPrice: String? = null,
    @SerializedName("id")
    val id: Int? = 0,
    @SerializedName("product_name")
    val productName: String? = null,
    @SerializedName("xp_points")
    val xpPoints: Int? = 0,
    var isSelected: Int =0,
    var isXpLayoutVisible:Boolean=false
):Parcelable