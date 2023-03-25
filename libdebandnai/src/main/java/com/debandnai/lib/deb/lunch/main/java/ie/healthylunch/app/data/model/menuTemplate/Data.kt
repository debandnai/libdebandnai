package ie.healthylunch.app.data.model.menuTemplate

import com.google.gson.annotations.SerializedName

data class Data(@SerializedName("menu_template_list")
                val menuTemplateList: List<MenuTemplateListItem?>?,
                @SerializedName("menu_id")
                val menuId: Int ?= 0,

                @SerializedName("order_date")
                val orderDate: String ?= null,

                @SerializedName("week_day")
                val weekDay: String ?= null,


                )