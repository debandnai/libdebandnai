package ie.healthylunch.app.data.model.productInfoDetailsModel

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("product_desc") val productDesc: String? = null,
    @SerializedName("nutrition_values") val nutritionList: List<NutritionValuesItem?>? = ArrayList(),
    @SerializedName("product_id") val productId: Int? = 0,
    @SerializedName("i_button_image") val iButtonImage: String? = null,
    @SerializedName("packaging_type") val packagingType: Int? = 0,
    @SerializedName("i_button_heading") val iButtonHeading: String? = null,
    @SerializedName("allergen_list") val allergenList: List<String?>? = ArrayList()
)