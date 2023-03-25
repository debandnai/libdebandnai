package com.merkaaz.app.ui.dialogs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.merkaaz.app.R
import com.merkaaz.app.databinding.BottomSheetLayoutBinding
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.SharedPreff
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class BottomSheetDialogLanguage(val activity: Activity?) : BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetLayoutBinding

    @Inject
    lateinit var sharedPreff: SharedPreff

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_layout, container, false)
        binding.lifecycleOwner = this
        setLanguageCode()
        onViewClick()
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.SheetDialog
    }

    private fun setLanguageCode() {
        if (sharedPreff.getlanguage_code().equals(Constants.ENGLISH_CODE))
            setSelectedLanguageView(binding.llEnglish, binding.tvEnglish)
        else
            setSelectedLanguageView(binding.llPortuguese, binding.tvPortuguese)

    }

    private fun onViewClick() {
        binding.llPortuguese.setOnClickListener {
            sharedPreff.setLanguage(
                Constants.PORTUGUESE_CODE,
                Constants.PORTUGUESE_ID
            )
            setLanguageCode()
            dismiss()
            activity?.let {
                val intent = Intent(activity, activity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                activity.overridePendingTransition(0, 0)
            }

        }
        binding.llEnglish.setOnClickListener {
            sharedPreff.setLanguage(
                Constants.ENGLISH_CODE,
                Constants.ENGLISH_ID
            )
            setLanguageCode()
            dismiss()

            activity?.let {
                val intent = Intent(activity, activity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                activity.overridePendingTransition(0, 0)
            }

        }


    }

    private fun setSelectedLanguageView(linearLayout: LinearLayout, textView: TextView) {
        //set default color of all views
        binding.llEnglish.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.white_btn_bg)
        binding.llPortuguese.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.white_btn_bg)
        binding.tvPortuguese.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.address_unselect_color
            )
        )
        binding.tvEnglish.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.address_unselect_color
            )
        )

        //set the selected view
        linearLayout.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.teal_btn_bg)
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }


}