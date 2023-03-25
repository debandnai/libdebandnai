package com.merkaaz.app.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.data.model.ProfitLossList
import com.merkaaz.app.data.model.ProfitLossListModel
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.utils.Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE
import retrofit2.HttpException
import java.io.IOException

class ProfitLossPagingSource(
    private val api: RetroApi,
    private val body: JsonObject?,
    private val header: HashMap<String, String>
) :
    PagingSource<Int, ProfitLossList>() {
    val gson = Gson()
    private var res : JsonobjectModel? = null
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProfitLossList> {
        return try {
            val position = params.key ?: 1
            val startPositionItemNumber = (position-1) * TOTAL_NO_OF_PRODUCTS_PER_PAGE

            body!!.addProperty("first", startPositionItemNumber)
            println("json body.....${body.toString()}")
            res =  api.getProfitlossList(header,body)
            val result = res?.response?.data
            val profitLoss = object : TypeToken<ProfitLossListModel>() {}.type
            val profitLossListModel: ProfitLossListModel = gson.fromJson(result, profitLoss)


             LoadResult.Page(
                data = getList(profitLossListModel),
                prevKey =if (position == 1) null else position - 1,
                nextKey =if (profitLossListModel.totalCount == null)
                    null
                else if (position * TOTAL_NO_OF_PRODUCTS_PER_PAGE >= profitLossListModel.totalCount!!)
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

    private fun getList(profitLossListModel: ProfitLossListModel): List<ProfitLossList> {
        var arrayList: ArrayList<ProfitLossList> = ArrayList()
        if (profitLossListModel.profitLossList.size > 0)
            arrayList =  profitLossListModel.profitLossList


        return arrayList

    }

    override fun getRefreshKey(state: PagingState<Int, ProfitLossList>): Int? {
        return state.anchorPosition?.let {anchorPosition->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1)?:anchorPage?.nextKey?.minus(1)
        }
    }
}