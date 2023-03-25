package com.salonsolution.app.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.ReviewList
import com.salonsolution.app.data.network.UserApi
import com.salonsolution.app.utils.Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE
import retrofit2.HttpException
import java.io.IOException

class StaffReviewsPagingSource(
    private val userApi: UserApi,
    private val staffReviewsRequest: JsonObject
) : PagingSource<Int, ReviewList>() {

    override fun getRefreshKey(state: PagingState<Int, ReviewList>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ReviewList> {
        return try {
            val position = params.key ?: 1
            val startPositionItemNumber = (position-1) * TOTAL_NO_OF_PRODUCTS_PER_PAGE
            staffReviewsRequest.addProperty("first", startPositionItemNumber)
            Log.d("tag", "paging request: $staffReviewsRequest")
            val response = userApi.staffReviewList(staffReviewsRequest)
            val mData = response.body()?.response?.data
            val totalCount = mData?.totalReview ?: 0
            Log.d("tag", "load: $position..")
            val loadedItemCount = position* TOTAL_NO_OF_PRODUCTS_PER_PAGE
            val prevKey = if (position > 1) position - 1 else null
            val nextKey = if (loadedItemCount < totalCount) position + 1 else null
            LoadResult.Page(
                data = mData?.reviewList?: ArrayList(),
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            Log.d("tag", "IOException: ${e.message}..")
            LoadResult.Error(e)
        } catch (e: HttpException) {
            Log.d("tag", "HttpException: ${e.message}..")
            LoadResult.Error(e)
        }
    }


}