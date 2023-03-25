package ie.healthylunch.app.data.model.quickViewOrderDayModel

import com.google.gson.annotations.SerializedName

data class DayDetails(@SerializedName("day_name")
                      val dayName: String = "",
                      @SerializedName("day_date")
                      val dayDate: String = "",
                      @SerializedName("day_number")
                      val dayNumber: Int)