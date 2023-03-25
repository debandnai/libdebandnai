package ie.healthylunch.app.paging


import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.transactionListPagingModel.TransactionList
import ie.healthylunch.app.data.model.transactionListPagingModel.TransactionListResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.utils.Constants.Companion.FIRST_TAG
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import ie.healthylunch.app.utils.Constants.Companion.TOTAL_NO_OF_ITEMS_PER_PAGE
import retrofit2.HttpException
import java.io.IOException

class TransactionsPagingSource(
    private val api: ApiInterface,
    private val body: JsonObject?,
    private val token: String
) :
    PagingSource<Int, TransactionList>() {
    val gson = Gson()
    private var res : TransactionListResponse? = null
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TransactionList> {
        return try {
            val position = params.key ?: STATUS_ONE
            val startPositionItemNumber = (position-1) * TOTAL_NO_OF_ITEMS_PER_PAGE
            body?.addProperty(FIRST_TAG, startPositionItemNumber)
            res =  api.transactionListPagingApi(body,token)
            val result = res?.response?.raws?.data
            val totalCount = result?.totalCount ?: STATUS_ZERO
            val loadedItemCount = position * TOTAL_NO_OF_ITEMS_PER_PAGE
            val prevKey = if (position > STATUS_ONE) position - STATUS_ONE else null
            val nextKey = if (loadedItemCount < totalCount) position + STATUS_ONE else null

            LoadResult.Page(
                data = getList(result?.transactionList),
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {

            LoadResult.Error(e)
        } catch (e: HttpException) {

            LoadResult.Error(e)
        }
    }

    private fun getList(transactionList: List<TransactionList>?): List<TransactionList> {
        var arrayList: ArrayList<TransactionList> = ArrayList()
        if (!transactionList.isNullOrEmpty())
            arrayList= transactionList as ArrayList<TransactionList>
        return  arrayList
    }

    override fun getRefreshKey(state: PagingState<Int, TransactionList>): Int? {
        return state.anchorPosition?.let {anchorPosition->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(STATUS_ONE)?:anchorPage?.nextKey?.minus(STATUS_ONE)
        }
    }
}