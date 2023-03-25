package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class StaffDetailsModel(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("staff_name") var staffName: String? = null,
    @SerializedName("about") var about: String? = null,
    @SerializedName("total_review") var totalReview: Int? = null,
    @SerializedName("avg_review") var avgReview: String? = null,
    @SerializedName("five_star") var fiveStar: String? = null,
    @SerializedName("four_star") var fourStar: String? = null,
    @SerializedName("three_star") var threeStar: String? = null,
    @SerializedName("two_star") var twoStar: String? = null,
    @SerializedName("one_star") var oneStar: String? = null,
    @SerializedName("images") var images: ArrayList<StaffImages> = arrayListOf()
)

data class StaffImages(

    @SerializedName("image_id") var imageId: Int? = null,
    @SerializedName("image_link") var imageLink: String? = null,
    @SerializedName("is_default") var isDefault: String? = null

)