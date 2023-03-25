package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class RecentServiceListModel(
    @SerializedName("recent_service_list") var recentServiceList: ArrayList<BookedServiceList> = arrayListOf()
)