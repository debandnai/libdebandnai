package com.merkaaz.app.ui.dialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.merkaaz.app.R
import com.merkaaz.app.databinding.PictureUploadDialogLayoutBinding
import com.merkaaz.app.interfaces.CaptureImageInterface


class BottomsheetdialogImage(val activity: Activity?,val captureImageInterface: CaptureImageInterface) :
    BottomSheetDialogFragment() {

    lateinit var binding: PictureUploadDialogLayoutBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.picture_upload_dialog_layout,container,false)
        binding.lifecycleOwner = this

        click()
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.SheetDialog
    }


    private fun click() {
        binding.galleryLayout.setOnClickListener{
            captureImageInterface.capture_fromgallery()
            dismiss()
        }

        binding.cameraLayout.setOnClickListener{
            captureImageInterface.capture_fromcamera()
            dismiss()
        }

    }


}