package com.salonsolution.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salonsolution.app.R
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.databinding.FragmentLanguageChangeBinding
import com.salonsolution.app.ui.activity.AuthActivity
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.utils.Constants
import com.salonsolution.app.utils.SettingsUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LanguageChangeDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentLanguageChangeBinding

    @Inject
    lateinit var appSettingsPref: AppSettingsPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_language_change, container, false)
        setSelectedLanguageView()
        initView()
        return binding.root
    }

    private fun initView() {
        binding.rlEnglishLanguage.setOnClickListener {
            SettingsUtils.setLanguage(Constants.ENGLISH_CODE)
            setSelectedLanguageView()
//           (activity as? AuthActivity)?.changeLocale(Constants.ENGLISH_CODE)
//            (activity as? DashboardActivity)?.changeLocale(Constants.ENGLISH_CODE)
            dismiss()
        }
        binding.rlPortugueseLanguage.setOnClickListener {
            SettingsUtils.setLanguage(Constants.PORTUGUESE_CODE)
            setSelectedLanguageView()
//            (activity as? AuthActivity)?.changeLocale(Constants.PORTUGUESE_CODE)
//            (activity as? DashboardActivity)?.changeLocale(Constants.PORTUGUESE_CODE)
            dismiss()
        }
    }

    private fun setSelectedLanguageView(){

        context?.let {
            if(SettingsUtils.getLanguage() == Constants.ENGLISH_CODE ){
                binding.rlEnglishLanguage.background = ContextCompat.getDrawable(it,R.drawable.red_round_shape)
                binding.tvEnglish.setTextColor(ContextCompat.getColor(it,R.color.brand_color))
                binding.tvEnglish.typeface = ResourcesCompat.getFont(it,R.font.montserrat_semibold_600)

                binding.rlPortugueseLanguage.background = ContextCompat.getDrawable(it,R.drawable.white_round_shape)
                binding.tvPortuguese.setTextColor(ContextCompat.getColor(it,R.color.default_text_color))
                binding.tvPortuguese.typeface = ResourcesCompat.getFont(it,R.font.montserrat_medium_500)
            }else{
                binding.rlEnglishLanguage.background = ContextCompat.getDrawable(it,R.drawable.white_round_shape)
                binding.tvEnglish.setTextColor(ContextCompat.getColor(it,R.color.default_text_color))
                binding.tvEnglish.typeface = ResourcesCompat.getFont(it,R.font.montserrat_medium_500)

                binding.rlPortugueseLanguage.background = ContextCompat.getDrawable(it,R.drawable.red_round_shape)
                binding.tvPortuguese.setTextColor(ContextCompat.getColor(it,R.color.brand_color))
                binding.tvPortuguese.typeface = ResourcesCompat.getFont(it,R.font.montserrat_semibold_600)
            }
        }


    }


}