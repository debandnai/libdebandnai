package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

data class ProfitLossDetailsListModel(

    @SerializedName("order_date"         ) var orderDate         : String?                      = null,
    @SerializedName("num_of_items"       ) var numOfItems        : Int?                         = null,
    @SerializedName("order_cost_price"   ) var orderCostPrice    : String?                      = null,
    @SerializedName("order_gross_price"  ) var orderGrossPrice   : String?                      = null,
    @SerializedName("order_profit"       ) var orderProfit       : String?                      = null,
    @SerializedName("profitloss_product" ) var profitLossProductList : ArrayList<ProfitlossProduct> = arrayListOf()
)

data class ProfitlossProduct (

    @SerializedName("id"             ) var id            : Int?    = null,
    @SerializedName("product_name"   ) var productName   : String? = null,
    @SerializedName("product_image"  ) var productImage  : String? = null,
    @SerializedName("size"           ) var size          : String? = null,
    @SerializedName("quantity"       ) var quantity      : Int?    = null,
    @SerializedName("brand"          ) var brand         : String? = null,
    @SerializedName("cost_price"     ) var costPrice     : String? = null,
    @SerializedName("sell_price"     ) var sellPrice     : String? = null,
    @SerializedName("net_profit"     ) var netProfit     : String? = null,
    @SerializedName("profit_percent" ) var profitPercent : String? = null

)






