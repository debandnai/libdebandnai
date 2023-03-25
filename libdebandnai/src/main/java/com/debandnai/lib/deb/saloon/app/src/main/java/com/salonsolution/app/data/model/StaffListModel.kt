package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class StaffListModel(
    @SerializedName("staff_list") var staffList: ArrayList<StaffList> = arrayListOf(),
    @SerializedName("total_count") var totalCount: Int? = null,
    @SerializedName("any_staff_id") var anyStaffId: Int? = null
)

data class StaffList(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("staff_name") var staffName: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("about") var about: String? = null
)