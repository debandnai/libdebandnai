package com.merkaaz.app.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.merkaaz.app.data.model.FeatureProductListModel
import com.merkaaz.app.data.model.FeaturedData
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.RetroApi
import com.merkaaz.app.ui.activity.dashBoardActivity
import com.merkaaz.app.utils.Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AllProductPagingSource(
    private val api: RetroApi,
    private val body: JsonObject?,
    private val header: HashMap<String, String>
) :
    PagingSource<Int, FeaturedData>() {
    val gson = Gson()
    private var res : JsonobjectModel? = null
    @Inject
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FeaturedData> {
        return try {
            val position = params.key ?: 1
            val startPositionItemNumber = (position-1) * TOTAL_NO_OF_PRODUCTS_PER_PAGE
            body!!.addProperty("first", startPositionItemNumber)
            println("json body.....All ${body.toString()}")
            res = api.getAllProductList(header,body)
            val result = res?.response?.data
            println("product result....$result")
            val featuredProduct = object : TypeToken<FeatureProductListModel>() {}.type
            val featureProductListModel: FeatureProductListModel = gson.fromJson(result, featuredProduct)
          //  featureProductListModel.total_count?.let {  category_item.value=it}
            featureProductListModel.total_count?.let {  dashBoardActivity?.sharedPreff?.setTotalProductCount(it)}

//            val state =
//                ceil(featureProductListModel.total_count?.toDouble()
//                    ?.div(TOTAL_NO_OF_PRODUCTS_PER_PAGE) ?: 0.0)
//                    .toInt()
             LoadResult.Page(
                data = getList(featureProductListModel),
                prevKey = if (position == 1) null else position - 1,
                nextKey =
//                if (position == state)
                if (featureProductListModel.total_count == null)
                    null
                else if (position * TOTAL_NO_OF_PRODUCTS_PER_PAGE >= featureProductListModel.total_count!!)
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

    private fun getList(featureProductListModel: FeatureProductListModel): List<FeaturedData> {
        var arrayList: ArrayList<FeaturedData> = ArrayList()
        if (featureProductListModel.featureList.size > 0)
            arrayList =  featureProductListModel.featureList
        else if (featureProductListModel.category_list.size > 0)
            arrayList =  featureProductListModel.category_list
        else if (featureProductListModel.bestSellerList.size > 0)
            arrayList =  featureProductListModel.bestSellerList
        else if (featureProductListModel.productList.size > 0)
            arrayList =  featureProductListModel.productList
        return arrayList

    }

    override fun getRefreshKey(state: PagingState<Int, FeaturedData>): Int? {
        return state.anchorPosition?.let {anchorPosition->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1)?:anchorPage?.nextKey?.minus(1)
        }
    }
}