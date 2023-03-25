package ie.healthylunch.app.data.model.productListByMenuTemplateModel

import com.google.gson.annotations.SerializedName

data class SelectedMenutemplateItem(@SerializedName("temp")
                                    val temp: Int = 0,
                                    @SerializedName("name")
                                    val name: String = "")