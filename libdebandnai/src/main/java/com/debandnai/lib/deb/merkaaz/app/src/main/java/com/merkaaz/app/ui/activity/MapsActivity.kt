package com.merkaaz.app.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.material.snackbar.Snackbar
import com.merkaaz.app.R
import com.merkaaz.app.adapter.TAG
import com.merkaaz.app.data.viewModel.MapViewModel
import com.merkaaz.app.databinding.ActivityMapsBinding
import com.merkaaz.app.interfaces.DialogCallback
import com.merkaaz.app.network.Response
import com.merkaaz.app.ui.base.BaseActivity
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.Constants.ADDRESS1
import com.merkaaz.app.utils.Constants.ADDRESS2
import com.merkaaz.app.utils.Constants.MY_DELIVERY_ADDRESS
import com.merkaaz.app.utils.Constants.PERMISSION_MSG
import com.merkaaz.app.utils.Constants.REFERENCE_POINT
import com.merkaaz.app.utils.MethodClass
import com.merkaaz.app.utils.MethodClass.getLatLonFromAddress
import com.merkaaz.app.utils.MethodClass.replaceCityPostalCodeStateFromAddress
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MapsActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraMoveCanceledListener,
    GoogleMap.OnCameraIdleListener {


    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var mapViewModel: MapViewModel
    private var isPermissionGranted: Boolean? = false
    private var currentLocation: Location? = null
    private lateinit var locationManager: LocationManager
    private var locationByNetwork: Location? = null
    private var locationByGps: Location? = null
    private var lastLatLong: LatLng? = null
    private var hasGps: Boolean? = null
    private var hasNetwork: Boolean? = null
    private var resolutionForResult: ActivityResultLauncher<IntentSenderRequest>? = null
    private var loader: Dialog? = null
    var isMyDeliveryAddressSideMenu = false
    private var firstTime: Boolean = true
    private var getStreet1 = ""
    private var getStreet2 = ""

    @Inject
    lateinit var commonFunctions: CommonFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps)
        //set the activity of activity variable in layout
//        binding.activity = this
        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]
        binding.viewModel = mapViewModel
        binding.lifecycleOwner = this
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        initialise()
        loadMap()
        getIntentValue()
        onViewClick()

        observeData()

    }

    private fun initialise() {
        loader = MethodClass.custom_loader(this, getString(R.string.please_wait))
    }

    private fun getIntentValue() {
        if (intent.extras != null) {
            if (intent.hasExtra(ADDRESS1)) {
                mapViewModel.street1.value = intent.getStringExtra(ADDRESS1)
                getStreet1 = intent.getStringExtra(ADDRESS1).toString()
            }

            if (intent.hasExtra(ADDRESS2)) {
                mapViewModel.street2.value = intent.getStringExtra(ADDRESS2)
                getStreet2 = intent.getStringExtra(ADDRESS2).toString()
            }

            if (intent.hasExtra(REFERENCE_POINT))
                mapViewModel.referencePoint.value = intent.getStringExtra(REFERENCE_POINT)

            isMyDeliveryAddressSideMenu = intent.hasExtra(MY_DELIVERY_ADDRESS)
            if (isMyDeliveryAddressSideMenu) {
                binding.clConfirmLocation.visibility = View.GONE
            } else {
                binding.clConfirmLocation.visibility = View.VISIBLE
            }
        }

    }

    private fun onViewClick() {
        binding.btnConfirmLocation.setOnClickListener {
            //   lifecycleScope.launch(Dispatchers.IO) {
            lastLatLong?.let { latLong ->
                if (mapViewModel.mapAddress.value.isNullOrEmpty()) {
                    mapViewModel.mapAddress.value =
                        MethodClass.getFullAddressFromLatLon(
                            this@MapsActivity,
                            mMap.cameraPosition.target.latitude,
                            mMap.cameraPosition.target.longitude
                        )
                    lastLatLong = LatLng(
                        mMap.cameraPosition.target.latitude,
                        mMap.cameraPosition.target.longitude
                    )
                    mMap.uiSettings.isMyLocationButtonEnabled = true


                }
                var street1 = ""
                var street2 = ""

                mapViewModel.mapAddress.value?.let { mapAddress ->
                    val afterSplitMapAddress = mapAddress.split(",").toTypedArray()
                    if (afterSplitMapAddress.isNotEmpty()) {
                        repeat(afterSplitMapAddress.count()) { c ->

                            if ((afterSplitMapAddress.size / 2) > c)
                                street1 += replaceCityPostalCodeStateFromAddress(
                                    this@MapsActivity,
                                    afterSplitMapAddress[c],
                                    latLong.latitude,
                                    latLong.longitude
                                )
                            else
                                street2 += replaceCityPostalCodeStateFromAddress(
                                    this@MapsActivity,
                                    afterSplitMapAddress[c],
                                    latLong.latitude,
                                    latLong.longitude
                                )
                        }

                    }
                }

                Log.e("street1", street1)
                Log.e("street2", street2)

                intent.putExtra(Constants.LAT, latLong.latitude)
                intent.putExtra(Constants.LONG, latLong.longitude)
                intent.putExtra(ADDRESS1, street1)
                intent.putExtra(ADDRESS2, street2)
                intent.putExtra(REFERENCE_POINT, mapViewModel.referencePoint.value)
                if (!mapViewModel.street1.value.isNullOrEmpty()) {
//                    val loginModel = SharedPreff.get_logindata(this@MapsActivity)
                    val token = loginModel.token
                    if (token.isNullOrBlank()) {
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else
                        mapViewModel.upDateLocation(
                            getString(R.string.luanda),
                            street1,
                            street2,
                            mapViewModel.referencePoint.value,
                            getString(R.string.angola),
                            latLong.latitude.toString(),
                            latLong.longitude.toString()
                        )

                } else {
                    //binding.tvStree1.error = getString(R.string.street1_required)
                    Toast.makeText(
                        this@MapsActivity,
                        getString(R.string.street1_required),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            //   }


        }


        binding.tvStree1.doOnTextChanged { _, _, _, _ ->
            if (!mapViewModel.street1.value.isNullOrBlank()) {
                binding.tvStree1.error = null
                binding.btnConfirmLocation.isEnabled = true
            }

        }


        binding.btnLocate.setOnClickListener {
            if (!isMyDeliveryAddressSideMenu) {
                onLocationButtonClick()
            }
        }


        //Back Button
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }


    }

    private fun observeData() {
        //For Update Location
        mapViewModel.userLocationUpdate.observe(this) {
            when (it) {
                is Response.Loading -> {
                    loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->

                        loader?.dismiss()
                        loginModel.address1 = intent.getStringExtra(ADDRESS1)
                        loginModel.address2 = intent.getStringExtra(ADDRESS2)
                        loginModel.referencePoint = intent.getStringExtra(REFERENCE_POINT)
                        loginModel.latitude = intent.getStringExtra(Constants.LAT)
                        loginModel.longitude = intent.getStringExtra(Constants.LONG)

                        sharedPreff.store_logindata(loginModel)
                        if (data.response?.status?.actionStatus == true) {
                            MethodClass.custom_msg_dialog_callback(
                                this, resources.getString(R.string.update_location_success),
                                object : DialogCallback {
                                    override fun dialog_clickstate() {
                                        finish()
                                    }
                                }
                            )

                        }

                    }

                }
                is Response.Error -> {
                    loader?.dismiss()
                    commonFunctions.showErrorMsg(this, it.errorCode, it.errorMessage)
                }
            }
        }
    }


    private fun onLocationButtonClick() {
        hideKeyBoard(binding.btnLocate)
        //Check Street1 is empty or not
        if (!mapViewModel.street1.value.isNullOrEmpty()) {
            binding.btnConfirmLocation.setBackgroundResource(R.drawable.botton_shape_till_color)
            //Locate button Click

            //User Address
            val getAddressList = MethodClass.getAddressList(
                this@MapsActivity,
                mapViewModel.street1.value,
                mapViewModel.street2.value,
                mapViewModel.referencePoint.value
            )


            val userAddress = TextUtils.join(", ", getAddressList)
            //Get lat lon from user address and check Lat Lon are null or not
            if (getLatLonFromAddress(this@MapsActivity, userAddress) == null) {
                /*Snackbar.make(
                    binding.btnLocate,
                    getString(R.string.location_not_found),
                    Snackbar.LENGTH_LONG
                ).show()*/
            } else
                lastLatLong = getLatLonFromAddress(this@MapsActivity, userAddress)
            Log.d(TAG, "onLocationButtonClick: ttttt " + lastLatLong)

            //Set google map location on map
            lastLatLong?.let { CameraUpdateFactory.newLatLng(it) }
                ?.let { mMap.moveCamera(it) }


        } else {
            Snackbar.make(
                binding.btnLocate,
                getString(R.string.street1_required),
                Snackbar.LENGTH_LONG
            ).show()

            binding.btnConfirmLocation.setBackgroundResource(R.drawable.botton_shape_gray_color)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            Toast.makeText(this, PERMISSION_MSG, Toast.LENGTH_SHORT).show()
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            isPermissionGranted = true
            enableLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocation() {

//        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        checkLocationService()

        mMap.setOnCameraMoveStartedListener(this)
        mMap.setOnCameraIdleListener(this)
        mMap.setOnCameraMoveListener(this)

    }


    @RequiresApi(Build.VERSION_CODES.N)
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        isPermissionGranted = when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                true
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                true
            }
            else -> {
                false
                // No location access granted.
            }
        }
        if (isPermissionGranted == true)
            enableLocation()
        //else
        //defaultMapLocationSetUp()
        //setCurrentLocation()
    }

    private fun loadMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        resolutionForResult =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        fetchCurrentLocation()
                    }
                    RESULT_CANCELED -> {
                        fetchCurrentLocation()
                    }
                }

            }


    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isScrollGesturesEnabled = false

        requestPermission()
    }


    @SuppressLint("MissingPermission")
    private fun fetchCurrentLocation() {
        if (lastLatLong == null) {
            hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


            if (hasNetwork as Boolean) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F,
                    networkLocationListener
                )
            }
            if (hasGps as Boolean) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0F,
                    gpsLocationListener
                )
            }

            val lastKnownLocationByGps =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            lastKnownLocationByGps?.let {
                locationByGps = lastKnownLocationByGps
            }
            //------------------------------------------------------//
            val lastKnownLocationByNetwork =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            lastKnownLocationByNetwork?.let {
                locationByNetwork = lastKnownLocationByNetwork
            }
            //------------------------------------------------------//
            if (locationByGps != null) {
                currentLocation = locationByGps
                currentLocation?.let { setLocation(it) }

                // use latitude and longitude as per your need
            } else if (locationByNetwork != null) {
                currentLocation = locationByNetwork
                currentLocation?.let { setLocation(it) }


            }

        }

        currentLocation?.let { location ->

        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false
            })
            .addOnSuccessListener { location: Location? ->
                if (location == null)
                    Toast.makeText(this, "Cannot get location.", Toast.LENGTH_SHORT).show()
                else {
                    if (firstTime) {
                        lastLatLong = LatLng(location.latitude, location.longitude)
                        setCurrentLocation()
                        firstTime = false
                    }
                }

            }

        //defaultMapLocationSetUp()
    }

    private fun defaultMapLocationSetUp() {
        //Default Luanda location
        lastLatLong = LatLng(-8.888267577135055, 13.204191774129868)
        lastLatLong?.let {
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    it,
                    Constants.ZOOM_LEVEL
                )
            )
        }
    }

    private fun setCurrentLocation() {
        if (getStreet1.isEmpty() && getStreet2.isEmpty()) {
            getAndSetStreetNames()
            lastLatLong = LatLng(-8.888267577135055, 13.204191774129868)
        }
        else {
            //User Address
            val getAddressList = MethodClass.getAddressList(
                this@MapsActivity,
                mapViewModel.street1.value,
                mapViewModel.street2.value,
                mapViewModel.referencePoint.value
            )


            val userAddress =
                "${resources.getString(R.string.luanda)}, ${resources.getString(R.string.angola)}, $getAddressList"

            lastLatLong = getLatLonFromAddress(this@MapsActivity, userAddress)
        }
        lastLatLong?.let {
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    it,
                    Constants.ZOOM_LEVEL
                )
            )
        }

    }

    private fun checkLocationService() {
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10 * 1000
        locationRequest.fastestInterval = 2 * 1000


        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        // builder.setAlwaysShow(true)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this) {
            it.locationSettingsStates
            fetchCurrentLocation()
        }

        task.addOnFailureListener(this) { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    resolutionForResult?.launch(
                        IntentSenderRequest.Builder(e.resolution).build()
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }

            }
        }
    }


    override fun onCameraMoveStarted(p0: Int) {
        mMap.clear()
    }


    override fun onCameraMove() {

    }

    override fun onCameraMoveCanceled() {
    }

    override fun onCameraIdle() {
        //Get Map Address
        val target: LatLng = mMap.cameraPosition.target
        mapViewModel.mapAddress.value =
            MethodClass.getFullAddressFromLatLon(
                this@MapsActivity,
                target.latitude,
                target.longitude
            )
        lastLatLong = LatLng(target.latitude, target.longitude)
        lastLatLong = target

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    private val gpsLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            setLocation(location)
            Log.d("Location gps", location.latitude.toString() + location.longitude.toString())
            if (firstTime) {
                lastLatLong = LatLng(location.latitude, location.longitude)
                setCurrentLocation()
                firstTime = false
            }


        }

        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    //------------------------------------------------------//
    private val networkLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            setLocation(location)
            Log.d("Location network", location.latitude.toString() + location.longitude.toString())
            if (firstTime) {
                lastLatLong = LatLng(location.latitude, location.longitude)
                setCurrentLocation()
                firstTime = false
            }

        }

        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun setLocation(location: Location?) {
        if (lastLatLong == null)
            location?.let {
                locationByNetwork = location
                lastLatLong = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        lastLatLong!!,
                        Constants.ZOOM_LEVEL
                    )
                )
            }
    }

    private fun hideKeyBoard(view: View) {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun getAndSetStreetNames() {
        lastLatLong?.let { latLong ->
            if (mapViewModel.mapAddress.value.isNullOrEmpty()) {
                mapViewModel.mapAddress.value =
                    MethodClass.getFullAddressFromLatLon(
                        this@MapsActivity,
                        latLong.latitude,
                        latLong.longitude
                    )

                //mMap.uiSettings.isMyLocationButtonEnabled = true


            }
            var street1 = ""
            var street2 = ""

            mapViewModel.mapAddress.value?.let { mapAddress ->
                val afterSplitMapAddress = mapAddress.split(",").toTypedArray()
                if (afterSplitMapAddress.isNotEmpty()) {
                    repeat(afterSplitMapAddress.count()) { c ->

                        if ((afterSplitMapAddress.size / 2) > c)
                            street1 += replaceCityPostalCodeStateFromAddress(
                                this@MapsActivity,
                                afterSplitMapAddress[c],
                                latLong.latitude,
                                latLong.longitude
                            )
                        else
                            street2 += replaceCityPostalCodeStateFromAddress(
                                this@MapsActivity,
                                afterSplitMapAddress[c],
                                latLong.latitude,
                                latLong.longitude
                            )
                    }

                }
            }



            mapViewModel.street1.value = street1
            mapViewModel.street2.value = street2
            Log.e("street1", street1)
            Log.e("street2", street2)

        }

    }

}