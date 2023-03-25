package com.merkaaz.app.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.merkaaz.app.R
import com.merkaaz.app.data.model.*
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE
import com.merkaaz.app.utils.MethodClass
import retrofit2.HttpException
import java.io.IOException

class ShopByCategoryPagingSource(
    private val api: RetroApi,
    private val body: JsonObject?,
    private val header: HashMap<String, String>
) :
    PagingSource<Int, ShopByCategoryList>() {
    val gson = Gson()
    private var res : JsonobjectModel? = null
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ShopByCategoryList> {
        return try {
            val position = params.key ?: 1
            val startPositionItemNumber = (position-1) * TOTAL_NO_OF_PRODUCTS_PER_PAGE

            body!!.addProperty("first", startPositionItemNumber)
            println("json body.....${body.toString()}")
            res =  api.getShopByCategoryList(header,body)
            val result = res?.response?.data
            val category = object : TypeToken<ShopByCategoryListModel>() {}.type
            val categoryListModel: ShopByCategoryListModel = gson.fromJson(result, category)


             LoadResult.Page(
                data = getList(categoryListModel),
                prevKey =if (position == 1) null else position - 1,
                nextKey =if (categoryListModel.totalCount == null)
                    null
                else if (position * TOTAL_NO_OF_PRODUCTS_PER_PAGE >= categoryListModel.totalCount!!)
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

    private fun getList(categoryList: ShopByCategoryListModel): List<ShopByCategoryList> {
        var arrayList: ArrayList<ShopByCategoryList> = ArrayList()
        if (categoryList.categoryList.size > 0)
            arrayList =  categoryList.categoryList
        return arrayList

    }

    override fun getRefreshKey(state: PagingState<Int, ShopByCategoryList>): Int? {
        return state.anchorPosition?.let {anchorPosition->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1)?:anchorPage?.nextKey?.minus(1)
        }
    }
}