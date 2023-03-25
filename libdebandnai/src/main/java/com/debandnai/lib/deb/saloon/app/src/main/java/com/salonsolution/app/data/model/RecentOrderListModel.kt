package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class RecentOrderListModel(
    @SerializedName("recent_order_list") var recentOrderList: ArrayList<UpcomingOrderList> = arrayListOf()
)