package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class UpcomingOrderListModel(
    @SerializedName("upcoming_order_list") var upcomingOrderList: ArrayList<UpcomingOrderList> = arrayListOf()
)

data class UpcomingOrderList(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("service_name") var serviceName: String? = null,
    @SerializedName("service_image") var serviceImage: String? = null,
    @SerializedName("booking_date") var bookingDate: String? = null
)