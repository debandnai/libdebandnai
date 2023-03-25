package com.merkaaz.app.data.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Location
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.repository.MapRepository
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.SharedPreff
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: MapRepository,
    private val commonFunctions: CommonFunctions,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private  val context: Context
) : ViewModel() {
        var mapAddress: MutableLiveData<String> = MutableLiveData("")
    var street1: MutableLiveData<String> = MutableLiveData("")
    var street2: MutableLiveData<String> = MutableLiveData("")
    var referencePoint: MutableLiveData<String> = MutableLiveData("")
    var city: MutableLiveData<String> = MutableLiveData(Constants.DEFAULT_CITY)
    var country: MutableLiveData<String> = MutableLiveData(Constants.DEFAULT_COUNTRY)
    var mMapLatLng = ObservableField<LatLng>()

    var lat: Double = 0.0
    private var lon: Double = 0.0
    private val requestCheckSettings: Int = 101
    private lateinit var mMap: GoogleMap
    private var permissionFineLocation = 101

    private lateinit var lastLocation: Location
    private lateinit var locationRequest: LocationRequest


    //For Location Update
    val userLocationUpdate: LiveData<Response<JsonobjectModel>>
        get() = repository.locationUpdate

    fun upDateLocation(
        city: String,
        //state: String,
        street_1: String?,
        street_2: String?,
        referencePoint: String?,
        country: String?,
        latitude: String?,
        longitude: String?
    ) {
        val json = commonFunctions.commonJsonData()
        json.addProperty("city", city)
        //json.addProperty("state", state)
        json.addProperty("address1", street_1)
        json.addProperty("address2", street_2)
        json.addProperty("referrence_point", referencePoint)
        json.addProperty("country", country)
        json.addProperty("latitude", latitude)
        json.addProperty("longitude", longitude)
        viewModelScope.launch {
            repository.locationUpdateResponse(json, commonFunctions.commonHeader())
        }
    }



}