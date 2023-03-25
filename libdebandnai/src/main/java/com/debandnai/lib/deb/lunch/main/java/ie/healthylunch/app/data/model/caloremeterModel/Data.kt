package ie.healthylunch.app.data.model.caloremeterModel

import com.google.gson.annotations.SerializedName

data class Data(@SerializedName("nutrient")
                val nutrient: List<NutrientItem>?)