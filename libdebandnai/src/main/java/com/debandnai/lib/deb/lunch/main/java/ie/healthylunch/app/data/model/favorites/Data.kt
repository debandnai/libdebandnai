package ie.healthylunch.app.data.model.favorites

import com.google.gson.annotations.SerializedName

data class Data(@SerializedName("favourite_orders")
                val favouriteOrders: List<FavouriteOrdersItem?>? = ArrayList()
                )