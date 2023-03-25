package ie.healthylunch.app.data.model.productListByMenuTemplateModel

import com.google.gson.annotations.SerializedName

data class Data(@SerializedName("selected_menutemplate")
                val selectedMenutemplate: List<SelectedMenutemplateItem>?,
                @SerializedName("menutemplate_details")
                val menutemplateDetails: List<MenuTemplateDetailsItem>?)