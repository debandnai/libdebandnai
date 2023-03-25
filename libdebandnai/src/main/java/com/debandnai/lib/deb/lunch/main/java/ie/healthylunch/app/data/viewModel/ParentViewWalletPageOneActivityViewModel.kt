package ie.healthylunch.app.data.viewModel

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.cardListModel.CardListResponse
import ie.healthylunch.app.data.model.deleteCardModel.DeleteCardResponse
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.ParentViewWalletPageOneActivityRepository
import ie.healthylunch.app.ui.*
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.TOKEN
import kotlinx.coroutines.launch
import java.util.*

class ParentViewWalletPageOneActivityViewModel(
    private val repository: ParentViewWalletPageOneActivityRepository
) : ViewModel() {

    var walletBalance: MutableLiveData<String> = MutableLiveData()
    var cardNumber: MutableLiveData<String> = MutableLiveData()
    var isCardAdded: MutableLiveData<Boolean> = MutableLiveData()
    var brandName: MutableLiveData<String> = MutableLiveData()


    //card List
    val _cardListResponse: SingleLiveEvent<Resource<CardListResponse>?> =
        SingleLiveEvent()
    var cardListResponse: SingleLiveEvent<Resource<CardListResponse>?>? = null
        get() = _cardListResponse

    var from:String?=null

    fun cardList(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _cardListResponse.value = repository.cardList(jsonObject, token)
    }


    //delete card
    val _deleteCardResponse: SingleLiveEvent<Resource<DeleteCardResponse>?> =
        SingleLiveEvent()
    var deleteCardResponse: SingleLiveEvent<Resource<DeleteCardResponse>?>? = null
        get() = _deleteCardResponse

    fun deleteCard(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _deleteCardResponse.value = repository.deleteCard(jsonObject, token)
    }

}