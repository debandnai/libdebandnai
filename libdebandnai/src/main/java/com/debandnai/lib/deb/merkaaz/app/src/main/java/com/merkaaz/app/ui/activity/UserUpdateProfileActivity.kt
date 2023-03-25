package com.merkaaz.app.ui.activity


import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.merkaaz.app.BuildConfig
import com.merkaaz.app.R
import com.merkaaz.app.data.viewModel.UserProfileUpdateViewModel
import com.merkaaz.app.databinding.ActivityUserProfileUpdateBinding
import com.merkaaz.app.interfaces.CaptureImageInterface
import com.merkaaz.app.interfaces.DialogCallback
import com.merkaaz.app.network.Response
import com.merkaaz.app.ui.base.BaseActivity
import com.merkaaz.app.ui.dialogs.BottomsheetdialogImage
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.MethodClass
import com.merkaaz.app.utils.MethodClass.copyStream
import com.merkaaz.app.utils.MethodClass.getImageName
import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import javax.inject.Inject

@AndroidEntryPoint
class UserUpdateProfileActivity : BaseActivity(), CaptureImageInterface {

    private var loader: Dialog? = null
    private var isPermissionGranted: Boolean? = false
    private lateinit var binding: ActivityUserProfileUpdateBinding
    private lateinit var userProfileUpdateViewModel: UserProfileUpdateViewModel
    private lateinit var captureImageInterface: CaptureImageInterface

    @Inject
    lateinit var commonFunctions: CommonFunctions

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile_update)
        userProfileUpdateViewModel = ViewModelProvider(this)[UserProfileUpdateViewModel::class.java]

        binding.viewModel = userProfileUpdateViewModel
        binding.lifecycleOwner = this
        captureImageInterface = this
        init()
        observe_data()
        request_permission()
        click()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun request_permission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED

        ) {
//            ActivityCompat.requestPermissions(this,  arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION), FINE_LOCATION_PERMISSION_REQUEST);


            locationPermissionRequest.launch(
                arrayOf(Manifest.permission.CAMERA)
            )
        } else {
            isPermissionGranted = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        isPermissionGranted = when {
            permissions.getOrDefault(Manifest.permission.CAMERA, false) -> {
                // Precise location access granted.
                true
            }

            else -> {
                false
                // No location access granted.
            }
        }
        if (isPermissionGranted == true)
            request_permission()
        else
            Toast.makeText(this, Constants.CAMERA_PERMISSION_MSG, Toast.LENGTH_SHORT).show()
    }

    private fun click() {
        binding.btnCancel.setOnClickListener { finish() }
        binding.imgBack.setOnClickListener { finish() }
        binding.imgCamera.setOnClickListener {
//            showPictureDialog(R.style.DialogAnimation);
            val modalBottomSheet = BottomsheetdialogImage(this, this)
            modalBottomSheet.isCancelable = true
            modalBottomSheet.show(supportFragmentManager, "")
        }

        binding.imgHelp.setOnClickListener {
            startActivity(
                Intent(
                    this@UserUpdateProfileActivity,
                    CustomerServiceActivity::class.java
                )
            )
        }
    }

    private fun init() {
        loader = MethodClass.custom_loader(this, getString(R.string.please_wait))
    }


    private fun observe_data() {
        userProfileUpdateViewModel.updateUserProfileLiveData.observe(this) {
            when (it) {
                is Response.Loading -> {
                    loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->

                        loader?.dismiss()
                        if (data.response?.status?.actionStatus == true) {
//                            Toast.makeText(this, data.response?.status?.msg, Toast.LENGTH_SHORT).show()
                            MethodClass.custom_msg_dialog_callback(
                                this, resources.getString(R.string.update_profile_success),
                                object : DialogCallback {
                                    override fun dialog_clickstate() {
                                        onBackPressed()
                                    }
                                },
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


    override fun capture_fromcamera() {
        userProfileUpdateViewModel.photoFile = createImageFile()
        // Continue only if the File was successfully created
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
        if (userProfileUpdateViewModel.photoFile != null) {
            val photoURI = FileProvider.getUriForFile(
                this, BuildConfig.APPLICATION_ID + ".provider",
                userProfileUpdateViewModel.photoFile!!
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//            takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 2);
            cameraLaunch.launch(takePictureIntent)
        }
    }

    var cameraLaunch = registerForActivityResult(StartActivityForResult()) { result ->
        try {
            if (result.resultCode == RESULT_OK /*&& result.data != null*/) {
                val uri = Uri.fromFile(userProfileUpdateViewModel.photoFile)
                binding.ivUserImg.setImageURI(uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun capture_fromgallery() {
        galleryLaunch.launch("image/*")
    }

    var galleryLaunch = registerForActivityResult(GetContent()) { result ->
        binding.ivUserImg.setImageURI(result)
        userProfileUpdateViewModel.photoFile = result?.let {
            getRealPathFromURI(it)?.let { it1 ->
                File(it1)
            }?.let { it2 ->
                MethodClass.fileCompress(it2)
            }
        }
    }
    var mCurrentPhotoPath: String? = null

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val imageFileName = "profile_image"//""JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    private fun getRealPathFromURI(contentURI: Uri): String? {
        var result: String? = ""
        try {
            val fileName: String? = getImageName(contentURI, this)
            val file = File(externalCacheDir, fileName)
            file.createNewFile()
            FileOutputStream(file).use { outputStream ->
                contentResolver.openInputStream(contentURI).use { inputStream ->
                    inputStream?.let {
                        copyStream(
                            it,
                            outputStream
                        )
                    } //Simply reads input to output stream
                    outputStream.flush()
                }
            }
            result = Uri.fromFile(file).path
        } catch (e: java.lang.Exception) {
            e.toString()
        }
        return result
    }

}