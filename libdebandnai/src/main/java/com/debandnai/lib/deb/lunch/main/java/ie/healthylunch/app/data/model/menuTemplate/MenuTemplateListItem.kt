package ie.healthylunch.app.data.model.menuTemplate

import com.google.gson.annotations.SerializedName

data class MenuTemplateListItem(
    @SerializedName("menu_template_price")
    val menuTemplatePrice: String? = null,
    @SerializedName("menu_template_is_selected")
    val menuTemplateIsSelected: Int? = 0,
    @SerializedName("menu_template_id")
    val menuTemplateId: Int? = 0,
    @SerializedName("menu_template_name")
    val menuTemplateName: String? = null,
    @SerializedName("menu_template_i_xp_point")
    val menuTemplateIXpPoint: Int? = 0,
    @SerializedName("menu_template_icon")
    val menuTemplateIcon: String? = null,
    @SerializedName("menu_template_icon_web")
    val menuTemplateIconWeb: String? = null,
    @SerializedName("menu_template_i_desc")
    val menuTemplateIDesc: String? = null,
    var isXpLayoutVisible: Boolean = false
)