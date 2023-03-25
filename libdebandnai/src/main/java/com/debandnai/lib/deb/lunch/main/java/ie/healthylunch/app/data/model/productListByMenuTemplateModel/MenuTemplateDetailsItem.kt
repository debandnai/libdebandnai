package ie.healthylunch.app.data.model.productListByMenuTemplateModel

import com.google.gson.annotations.SerializedName

data class MenuTemplateDetailsItem(
    @SerializedName("menu_template_id") val menuTemplateId: Int? = 0,
    @SerializedName("menu_template_icon_active") val menuTemplateIconActive: Int? = 0,
    @SerializedName("total_calories") val totalCalories: Int? = 0,
    @SerializedName("respective_total_calories") val respectiveTotalCalories: Int? = 0,
    @SerializedName("menu_template_discount_price") val menuTemplateDiscountPrice: Int? = 0,
    @SerializedName("respective_total_sugar") val respectiveTotalSugar: Int? = 0,
    @SerializedName("total_sugar") val totalSugar: Int? = 0,
    @SerializedName("menu_template_price") val menuTemplatePrice: String? = null,
    @SerializedName("menu_template_icon_id") val menuTemplateIconId: Int? = 0,
    @SerializedName("map_menu_template_id") val mapMenuTemplateId: Int? = 0,
    @SerializedName("menu_template_xp_points") val menuTemplateXpPoints: Int? = 0,
    @SerializedName("menu_template_name") val menuTemplateName: String? = null,
    @SerializedName("menu_template_icon") val menuTemplateIcon: String? = null,
    @SerializedName("product_group") val productGroup: List<ProductGroupItem?>?
)