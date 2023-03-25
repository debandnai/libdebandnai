package ie.healthylunch.app.data.model.saveOrder

import com.google.gson.annotations.SerializedName

data class Data(

    @SerializedName("page_redirection")
    val pageRedirection: Int = 0,
    @SerializedName("dashboard_id")
    val dashboardId: Int = 0,

   )