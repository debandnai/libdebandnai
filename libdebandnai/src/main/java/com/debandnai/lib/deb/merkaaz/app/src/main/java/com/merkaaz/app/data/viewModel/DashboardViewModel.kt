package com.merkaaz.app.data.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.repository.CategoryRepository
import com.merkaaz.app.repository.DashBoardRepository
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.MethodClass
import com.merkaaz.app.utils.SharedPreff
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashBoardRepository: DashBoardRepository,
    private val categoryRepository: CategoryRepository,
    private val sharedPreff: SharedPreff,
    private val commonFunctions: CommonFunctions,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private  val context: Context
) : ViewModel() {
    var image: MutableLiveData<String> = MutableLiveData("")
    var userName: MutableLiveData<String> = MutableLiveData("")
    var userNameFirstChar: MutableLiveData<String> = MutableLiveData("")
    var mobileNo: MutableLiveData<String> = MutableLiveData("")
    var userLocation: MutableLiveData<String> = MutableLiveData("")
    var isUserApproved: MutableLiveData<Boolean> = MutableLiveData(false)
    var headerText: MutableLiveData<String> = MutableLiveData("")
    var isShowToolbar: MutableLiveData<Boolean> = MutableLiveData(true)
    var isShowSearchbar: MutableLiveData<Boolean> = MutableLiveData(true)
    var isShowBottomNavigation: MutableLiveData<Boolean> = MutableLiveData(true)
    var isSetCollapsingToolbar: MutableLiveData<Boolean> = MutableLiveData(true)
    var isShowToolbarOptionsWithLogo: MutableLiveData<Boolean> = MutableLiveData(true)
    var isShowHelpLogo: MutableLiveData<Boolean> = MutableLiveData(false)
    var pendingapprovalStat: MutableLiveData<Boolean> = MutableLiveData(true)
    var pendingapprovalStatForImageProfile: MutableLiveData<Boolean> = MutableLiveData(true)
    var parentCat_ID : String? = null
    var search_text: MutableLiveData<String?> = MutableLiveData("")
    var num_of_tabs : Int = 2
    var total_cart_count : Int = 0




    //For User Details
    val userDetailsLiveData: LiveData<Response<JsonobjectModel>>
        get() = dashBoardRepository.userDetails

    //For cart count
    val cartCountLiveData: LiveData<Response<JsonobjectModel>>
        get() = categoryRepository.cartCount

    //logout
    val logOutLiveData: LiveData<Response<JsonobjectModel>> get() = dashBoardRepository.logOutResponse

    fun getUserProfile() {
        viewModelScope.launch {
            dashBoardRepository.getUserDetailsResponse(commonFunctions.commonJsonData(), commonFunctions.commonHeader())

        }
    }

    fun getCartCount() {
        viewModelScope.launch {
            categoryRepository.getCartCountResponse(commonFunctions.commonJsonData(), commonFunctions.commonHeader())

        }
    }

    fun logOut(context: Context) {
        val jsonObject = commonFunctions.commonJsonData()
        jsonObject.addProperty("device_token", sharedPreff.getfirebase_token())
        viewModelScope.launch {
            dashBoardRepository.logout(commonFunctions.commonHeader(), jsonObject)
        }
    }




}