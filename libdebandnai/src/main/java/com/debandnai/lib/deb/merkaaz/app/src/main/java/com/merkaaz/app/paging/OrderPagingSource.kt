package com.merkaaz.app.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.merkaaz.app.R
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.data.model.OrderList
import com.merkaaz.app.data.model.OrderListModel
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE
import com.merkaaz.app.utils.MethodClass
import retrofit2.HttpException
import java.io.IOException

class OrderPagingSource(
    private val api: RetroApi,
    private val body: JsonObject?,
    private val header: HashMap<String, String>
) :
    PagingSource<Int, OrderList>() {
    val gson = Gson()
    private var res : JsonobjectModel? = null
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OrderList> {
        return try {
            val position = params.key ?: 1
            val startPositionItemNumber = (position-1) * TOTAL_NO_OF_PRODUCTS_PER_PAGE

            body!!.addProperty("first", startPositionItemNumber)
            println("json body.....${body.toString()}")
            res =  api.getOrderList(header,body)
            val result = res?.response?.data
            val order = object : TypeToken<OrderListModel>() {}.type
            val orderListModel: OrderListModel = gson.fromJson(result, order)


             LoadResult.Page(
                data = getList(orderListModel),
                prevKey =if (position == 1) null else position - 1,
                nextKey =if (orderListModel.totalCount == null)
                    null
                else if (position * TOTAL_NO_OF_PRODUCTS_PER_PAGE >= orderListModel.totalCount!!)
                    null
                else
                    position + 1
                )
        } catch (e: IOException) {

            LoadResult.Error(e)
        } catch (e: HttpException) {

            LoadResult.Error(e)
        }
    }

    private fun getList(orderListModel: OrderListModel): List<OrderList> {
        var arrayList: ArrayList<OrderList> = ArrayList()
        if (orderListModel.orderList.size > 0)
            arrayList =  orderListModel.orderList


        return arrayList

    }

    override fun getRefreshKey(state: PagingState<Int, OrderList>): Int? {
        return state.anchorPosition?.let {anchorPosition->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1)?:anchorPage?.nextKey?.minus(1)
        }
    }
}