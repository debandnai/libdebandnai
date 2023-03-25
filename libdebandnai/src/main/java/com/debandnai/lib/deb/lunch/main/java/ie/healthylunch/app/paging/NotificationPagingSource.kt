package ie.healthylunch.app.paging


import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.notificationListPagingModel.NotificationList
import ie.healthylunch.app.data.model.notificationListPagingModel.NotificationListResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.utils.Constants.Companion.FIRST_TAG
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import ie.healthylunch.app.utils.Constants.Companion.TOTAL_NO_OF_ITEMS_PER_PAGE
import retrofit2.HttpException
import java.io.IOException
import kotlin.math.log

class NotificationPagingSource(
    private val api: ApiInterface,
    private val body: JsonObject?,
    private val token: String
) :
    PagingSource<Int, NotificationList>() {
    val gson = Gson()
    private var res: NotificationListResponse? = null
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationList> {
        return try {
            val position = params.key ?: STATUS_ONE
            val startPositionItemNumber = (position-1) * TOTAL_NO_OF_ITEMS_PER_PAGE
            body?.addProperty(FIRST_TAG, startPositionItemNumber)
            res = api.notificationListPagingApi(body, token)
            val result = res?.response?.raws?.data
            Log.d("TAG_result", "load: $result ")
            val totalCount = result?.totalCount ?: STATUS_ZERO
            val loadedItemCount = position * TOTAL_NO_OF_ITEMS_PER_PAGE
            val prevKey = if (position > STATUS_ONE) position - STATUS_ONE else null
            val nextKey = if (loadedItemCount < totalCount) position + STATUS_ONE else null

            Log.d("TAG_load", "load: $body ")
            LoadResult.Page(
                data = getList(result?.notificationList),
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {

            LoadResult.Error(e)
        } catch (e: HttpException) {

            LoadResult.Error(e)
        }
    }

    private fun getList(notificationList: List<NotificationList>?): List<NotificationList> {
        var arrayList: ArrayList<NotificationList> = ArrayList()
        if (!notificationList.isNullOrEmpty())
            arrayList = notificationList as ArrayList<NotificationList>
        return arrayList
    }

    override fun getRefreshKey(state: PagingState<Int, NotificationList>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(STATUS_ONE) ?: anchorPage?.nextKey?.minus(STATUS_ONE)
        }
    }

}