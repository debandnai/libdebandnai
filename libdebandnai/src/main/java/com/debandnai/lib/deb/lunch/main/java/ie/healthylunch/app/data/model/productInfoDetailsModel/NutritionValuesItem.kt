package ie.healthylunch.app.data.model.productInfoDetailsModel

import com.google.gson.annotations.SerializedName

data class NutritionValuesItem(
    @SerializedName("nutrition_value") val nutritionValue: String? = null,
    @SerializedName("nutrient_id") val nutrientId: Int? = 0,
    @SerializedName("nutrient_name") val nutrientName: String? = null
)