package ie.healthylunch.app.data.model.favorites

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AllIdItem(@SerializedName("product_group_id")
                     val productGroupId: Int? = 0,
                     @SerializedName("product_id")
                     val productId: List<Integer?> = ArrayList()
                     ): Parcelable