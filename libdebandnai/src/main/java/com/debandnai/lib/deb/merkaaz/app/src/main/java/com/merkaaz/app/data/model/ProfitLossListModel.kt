package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

data class ProfitLossListModel(

    @SerializedName("total_count"     ) var totalCount     : Int?                      = null,
    @SerializedName("profitloss_list" ) var profitLossList : ArrayList<ProfitLossList> = arrayListOf()

)


data class ProfitLossList (

    @SerializedName("order_id") var orderId: Int?    = null,
    @SerializedName("order_date") var orderDate : String? = null,
    @SerializedName("no_of_items") var noOfItems : Int?    = null,
    @SerializedName("cost_price" ) var costPrice: String? = null,
    @SerializedName("gross_profit") var grossProfit: String? = null,
    @SerializedName("profit_percent") var profitPercent : Double?    = null,
    @SerializedName("generated_order_id") var generatedOrderId : String?    = null

)

