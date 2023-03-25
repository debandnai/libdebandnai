package ie.healthylunch.app.data.model.holidayResponse

import com.google.gson.annotations.SerializedName
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO


data class CalendarListResponse(@SerializedName("response") var response: Response? = null)
data class Response(@SerializedName("raws") var raws: Raws? = null)
data class Raws(

    @SerializedName("success_message") var successMessage: String? = null,
    @SerializedName("data") var data: Data? = null,
    @SerializedName("publish") var publish: Publish? = null

)

data class Publish(

    @SerializedName("version") var version: String? = null,
    @SerializedName("developer") var developer: String? = null

)

data class Data(

    @SerializedName("is_view_only") var isViewOnly: Int? = null,
    @SerializedName("calendar_disable_time") var calendarDisableTime: String? = null,
    @SerializedName("calendar") var calendar: ArrayList<ArrayList<CalendarData>?>? = null

)

data class CalendarData(

    @SerializedName("date") var date: String? = null,
    @SerializedName("ref_user_id") var ref_user_id: ArrayList<String>? = null,
    @SerializedName("order_for") var order_for: ArrayList<Int>? = null,
    @SerializedName("holiday_for") var holiday_for: ArrayList<Int>? = null,
    @SerializedName("order_status") var order_status: Int = STATUS_ZERO

)