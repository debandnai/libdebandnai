package ie.healthylunch.app.data.model.caloremeterModel

import com.google.gson.annotations.SerializedName

data class NutrientItem(@SerializedName("nutrient_value")
                        val nutrientValue: String = "",
                        @SerializedName("nutrient_id")
                        val nutrientId: Int = 0,
                        @SerializedName("nutrient_name")
                        val nutrientName: String = "")