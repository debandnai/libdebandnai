package com.salonsolution.app.data.paging

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.JsonObject
import com.salonsolution.app.data.model.StaffList
import com.salonsolution.app.data.network.UserApi
import com.salonsolution.app.utils.Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE
import retrofit2.HttpException
import java.io.IOException

class StaffListPagingSource(
    private val userApi: UserApi,
    private val staffListRequest: JsonObject,
    _totalStaffCountLiveData: MutableLiveData<String>,
    _anyStaffIdLiveData: MutableLiveData<Int>
) : PagingSource<Int, StaffList>() {
    private val totalListCount = _totalStaffCountLiveData
    private val anyStaffIdLiveData = _anyStaffIdLiveData

    override fun getRefreshKey(state: PagingState<Int, StaffList>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StaffList> {
        return try {
            val position = params.key ?: 1
            val startPositionItemNumber = (position-1) * TOTAL_NO_OF_PRODUCTS_PER_PAGE
            staffListRequest.addProperty("first", startPositionItemNumber)
            Log.d("tag", "paging request: $staffListRequest")
            val response = userApi.staffList(staffListRequest)
            val mData = response.body()?.response?.data
            val totalCount = mData?.totalCount ?: 0
            totalListCount.postValue(totalCount.toString())
            anyStaffIdLiveData.postValue(mData?.anyStaffId)
            Log.d("tag", "load: $position..")
            val loadedItemCount = position* TOTAL_NO_OF_PRODUCTS_PER_PAGE
            val prevKey = if (position > 1) position - 1 else null
            val nextKey = if (loadedItemCount < totalCount) position + 1 else null
            LoadResult.Page(
                data = mData?.staffList?: ArrayList(),
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