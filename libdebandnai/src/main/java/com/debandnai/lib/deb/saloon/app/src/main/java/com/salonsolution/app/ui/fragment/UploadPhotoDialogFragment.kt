package com.salonsolution.app.ui.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salonsolution.app.R
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.databinding.FragmentUploadPhotoDialogBinding
import com.salonsolution.app.utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class UploadPhotoDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentUploadPhotoDialogBinding
    private var profileUri: Uri? = null

    @Inject
    lateinit var appSettingsPref: AppSettingsPref

    companion object {
        const val GALLERY_URI = "gallery_uri"
        const val CAMERA_URI = "camera_uri"
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_upload_photo_dialog,
                container,
                false
            )
        //dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.galleryLayout.setOnClickListener {
            // if (checkStoragePermission()) {
            getImageFromGallery()
            // }
        }

        binding.cameraLayout.setOnClickListener {
            if (checkCameraPermission()) {
                launchCamera()
            }
        }
    }

    private fun checkStoragePermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    binding.root.context,
                    "" + Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        "" + Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                } else {
//                    showPermissionAlert(
//                        resources.getString(R.string.obr_needs_read_external_storage_permission_Enable_it_from_settings_question),
//                        true
//                    )
                    requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                return false
            } else {
                return true
            }
        } else {
            return true
        }
    }

    private val requestStoragePermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->

            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                getImageFromGallery()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                showPermissionAlert(
                    binding.root.context.getString(R.string.salon_needs_read_external_storage_permission_Enable_it_from_settings_question)
                )
            }

        }

    private fun getImageFromGallery() {
        try {
            getContent.launch("image/*")
        } catch (e: ActivityNotFoundException) {
            Log.d("tag", "Activity not found")
        }

    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Handle the returned Uri
            uri?.let {
                //
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    GALLERY_URI,
                    uri
                )
            }
            findNavController().popBackStack()

        }

    private fun showPermissionAlert(message: String) {
        val dialog = AlertDialog.Builder(binding.root.context, R.style.AlertDialogTheme)
            .setMessage(message)
            .setNegativeButton(binding.root.context.getString(R.string.cancel)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .setCancelable(false)
        dialog.setPositiveButton(binding.root.context.getString(R.string.setting)) { dialogInterface, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", binding.root.context.packageName, null)
            intent.data = uri
            startActivity(intent)
            dialogInterface.dismiss()
        }
        dialog.show()
    }

    private fun checkCameraPermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    binding.root.context,
                    "" + Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.CAMERA
                    )
                ) {
                    requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)

                } else {
//                    showPermissionAlert(
//                        resources.getString(R.string.obr_needs_camera_permission_Enable_it_from_settings_question),
//                        true
//                    )
                    requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
                return false
            } else {
                return true
            }
        } else {
            return true
        }
    }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                launchCamera()

            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                showPermissionAlert(binding.root.context.getString(R.string.salon_needs_camera_permission_Enable_it_from_settings_question))
            }

        }

    private fun launchCamera() {
        profileUri = FileUtils.getCaptureImageOutputUri(binding.root.context)
        getPicture.launch(FileUtils.getFileProviderUri(binding.root.context, profileUri))

    }

    private val getPicture = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            profileUri?.let { uri ->
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    CAMERA_URI,
                    uri
                )
            }
        }
        findNavController().popBackStack()
    }


}