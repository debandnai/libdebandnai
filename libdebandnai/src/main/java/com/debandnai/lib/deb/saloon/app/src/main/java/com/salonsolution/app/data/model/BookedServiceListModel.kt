package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class BookedServiceListModel(
    @SerializedName("booked_service_list") var bookedServiceList: ArrayList<BookedServiceList> = arrayListOf()
)

data class BookedServiceList(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("service_name") var serviceName: String? = null,
    @SerializedName("service_image") var serviceImage: String? = null
)