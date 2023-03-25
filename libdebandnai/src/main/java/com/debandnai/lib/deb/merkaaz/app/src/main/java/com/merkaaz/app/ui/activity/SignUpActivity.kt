package com.merkaaz.app.ui.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.merkaaz.app.R

import com.merkaaz.app.network.Response
import com.merkaaz.app.data.viewModel.SignUpViewModel
import com.merkaaz.app.databinding.ActivitySignUpBinding

import com.merkaaz.app.ui.base.BaseActivity
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.Constants.ADDRESS1
import com.merkaaz.app.utils.Constants.REFERENCE_POINT
import com.merkaaz.app.utils.Constants.ADDRESS2
import com.merkaaz.app.utils.MethodClass
import com.merkaaz.app.utils.SharedPreff

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class SignUpActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var binding: ActivitySignUpBinding
    private var loader: Dialog? = null
    var resultLauncher: ActivityResultLauncher<Intent>? = null


    @Inject
    lateinit var commonFunctions: CommonFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        //set the activity of activity variable in layout
        signUpViewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        binding.viewModel = signUpViewModel
        binding.lifecycleOwner = this

        //Google Map setup
        initialise()
        mapSetUp()
        observeData()
    }

    private fun initialise() {
        loader = MethodClass.custom_loader(this, getString(R.string.please_wait))
    }

    private fun observeData() {
        signUpViewModel.address.value = intent.getStringExtra(Constants.Address)
        signUpViewModel.signupLiveData.observe(this) {

            when (it) {
                is Response.Loading -> {
                    loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { it1 ->
                        val res = it1.response?.status?.msg
                        loader?.dismiss()
                        if (res.equals(Constants.shop_validate_msg, ignoreCase = true)) {
                            sharedPreff.setPhone(signUpViewModel.phoneNo.value?.toString())
                            // startActivity(Intent(this, CongratulationsActivity::class.java))
                            startActivity(
                                Intent(
                                    this,
                                    OtpActivity::class.java
                                ).putExtra(Constants.PAGE_TYPE, getString(R.string.sign_up))
                            )
                            finish()
                        }
                    }

                }
                is Response.Error -> {
                    loader?.dismiss()
                    commonFunctions.showErrorMsg(this, it.errorCode, it.errorMessage)
                }
            }
        }



        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data
                    data?.let {
                        val getAddressList = MethodClass.getAddressList(
                            this@SignUpActivity,
                            data.getStringExtra(Constants.ADDRESS1),
                            data.getStringExtra(ADDRESS2),
                            data.getStringExtra(Constants.REFERENCE_POINT)
                        )
                        signUpViewModel.address1.value = data.getStringExtra(Constants.ADDRESS1)
                        signUpViewModel.address2.value = data.getStringExtra(ADDRESS2)
                        signUpViewModel.referencePoint = data.getStringExtra(REFERENCE_POINT) ?: ""
                        signUpViewModel.address.value = TextUtils.join(", ", getAddressList)
                        val lat = data.getDoubleExtra(Constants.LAT, Constants.DEFAULT_LAT)
                        val long = data.getDoubleExtra(Constants.LONG, Constants.DEFAULT_LONG)
                        signUpViewModel.latitude.value = lat.toString()
                        signUpViewModel.longitude.value = long.toString()
                        set_map(lat, long)
                    }
                }
            }
        binding.tvAddress.setOnClickListener {
            mMap.clear()
            resultLauncher?.launch(
                Intent(this@SignUpActivity, MapsActivity::class.java)
                    .putExtra(ADDRESS1, signUpViewModel.address1.value)
                    .putExtra(ADDRESS2, signUpViewModel.address2.value)
                    .putExtra(REFERENCE_POINT, signUpViewModel.referencePoint)
            )
        }

//        binding.tvAddress.setOnClickListener {
//            mMap.clear()
//            resultLauncher?.launch(
//                Intent(this@SignUpActivity, MapsActivity::class.java).putExtra(
//                    Constants.ADDRESS1,
//                    signUpViewModel.address1.value
//                ).putExtra(Constants.ADDRESS2, signUpViewModel.address2.value)
//            )
//        }
    }


    private fun set_map(latitude: Double?, longitude: Double?) {
        val latlong = latitude?.let { lat ->
            longitude?.let { long ->
                LatLng(lat, long)
            }
        }

        latlong?.let {
            if (!latlong.equals(0.0)) {
                Log.d(
                    "Location sign up",
                    latlong.latitude.toString() + latlong.longitude.toString()
                )
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, Constants.zoom_level))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, Constants.ZOOM_LEVEL))
                //mMap.addMarker()8888888888888888888888
                val markerOptions = MarkerOptions().position(mMap.cameraPosition.target)
                mMap.addMarker(markerOptions)

                lifecycleScope.launch(Dispatchers.IO) {
                    signUpViewModel.city =
                        MethodClass.getCityName(this@SignUpActivity, latlong.latitude, latlong.longitude)
                    signUpViewModel.state =
                        MethodClass.getStateName(this@SignUpActivity, latlong.latitude, latlong.longitude)
                }
//                GlobalScope.launch(Dispatchers.IO) {
//                    signUpViewModel.city =
//                        MethodClass.getCityName(this@SignUpActivity, latlong.latitude, latlong.longitude)
//                    signUpViewModel.state =
//                        MethodClass.getStateName(this@SignUpActivity, latlong.latitude, latlong.longitude)
//                }

                /*signUpViewModel.referencePoint =
                    MethodClass.getPinCode(this, latlong.latitude, latlong.longitude)*/
            }
        }
    }

    //Google map setup
    private fun mapSetUp() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.fcv_map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        set_map(Constants.DEFAULT_LAT, Constants.DEFAULT_LONG)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}