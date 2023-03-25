package com.merkaaz.app.data.viewModel


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.google.gson.JsonObject
import com.merkaaz.app.network.Response
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.data.model.LoginModel
import com.merkaaz.app.repository.CategoryRepository
import com.merkaaz.app.repository.HomeRepository
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val categoryRepository: CategoryRepository,
    private val commonFunctions: CommonFunctions,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private  val context: Context
) : ViewModel() {
    var loginModel: LoginModel? = null

    //For Category List
    val categoryListLiveData: LiveData<Response<JsonobjectModel>>
        get() = homeRepository.categoryList

    //For Featured List
    val featuredListLiveData: LiveData<Response<JsonobjectModel>>
        get() = homeRepository.featuredList

    //For Best Seller List
    val bestSellerListLiveData: LiveData<Response<JsonobjectModel>>
        get() = homeRepository.bestSellerList

    //For snacks and branded product List
    val snacksAndBrandedLiveData: LiveData<Response<JsonobjectModel>>
        get() = homeRepository.snacksAndBrandedList

    //For add update product
    val addUpdateLiveData: LiveData<Response<JsonobjectModel>>
        get() = categoryRepository.add_update_product

    init {
        dashBoardAllProductList()
    }

    fun dashBoardAllProductList() {

        val categoryJsonObj = commonFunctions.commonJsonData()
        categoryJsonObj.addProperty("parent_cat_id", "0")
        categoryJsonObj.addProperty("first", "0")
        categoryJsonObj.addProperty("rows", Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE)

        //featured
        val featuredJsonObj = commonFunctions.commonJsonData()
        featuredJsonObj.addProperty("list_type", 1)
        featuredJsonObj.addProperty("first", "0")
        featuredJsonObj.addProperty("rows", Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE)

        //best seller
        val bestSellerJsonObj = commonFunctions.commonJsonData()
        bestSellerJsonObj.addProperty("list_type", 2)
        bestSellerJsonObj.addProperty("first", "0")
        bestSellerJsonObj.addProperty("rows", Constants.TOTAL_NO_OF_PRODUCTS_PER_PAGE)

        viewModelScope.launch {
            homeRepository.getCategoryListResponse(
                categoryJsonObj,
                commonFunctions.commonHeader()
            )
            homeRepository.getFeaturedListResponse(
                featuredJsonObj,
                commonFunctions.commonHeader()
            )
            homeRepository.getBestSellerListResponse(
                bestSellerJsonObj,
                commonFunctions.commonHeader()
            )

            homeRepository.getSnacksAndBrandedListResponse(
                commonFunctions.commonJsonData(),
                commonFunctions.commonHeader()
            )
        }
    }

    //Add Update product
    fun addUpdateProduct(jsonBody: JsonObject) {
        viewModelScope.launch {
            categoryRepository.add_update(jsonBody, commonFunctions.commonHeader())
        }
    }

    //remove product
    fun removeProduct(jsonBody: JsonObject) {
        viewModelScope.launch {
            categoryRepository.remove_product(jsonBody, commonFunctions.commonHeader())
        }
    }
}