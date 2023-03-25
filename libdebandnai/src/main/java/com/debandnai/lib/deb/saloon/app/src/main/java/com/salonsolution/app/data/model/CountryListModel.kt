package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class CountryListModel(
    @SerializedName("country_list") var countryList: ArrayList<CountryList> = arrayListOf()
)

data class CountryList(

    @SerializedName("country_name") var countryName: String? = null,
    @SerializedName("country_code") var countryCode: String? = null

)