package ie.healthylunch.app.data.model.singleDayOrderDateDisplay

import com.google.gson.annotations.SerializedName

data class Data(@SerializedName("first_order")
                val firstOrder: Int = 0,
                @SerializedName("order_next_week_date")
                val orderNextWeekDate: String = "",
                @SerializedName("order_next_date")
                val orderNextDate: String = "",
                @SerializedName("order_next_week_day")
                val orderNextWeekDay: String = "",
                @SerializedName("order_next_day")
                val orderNextDay: String = "")