package com.merkaaz.app.data.viewModel


import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.merkaaz.app.R
import com.merkaaz.app.utils.Constants.INPUT_FILTER_MAX_VALUE_PERCENTAGE
import com.merkaaz.app.utils.MethodClass
import com.merkaaz.app.utils.SharedPreff
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalculatorBottomSheetViewModel @Inject constructor(
    private val sharedPreff: SharedPreff,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private val context: Context?
) : ViewModel() {

    var sellPrice: MutableLiveData<String> = MutableLiveData("")
    var profitProfitPercentage: MutableLiveData<String> = MutableLiveData("")

    fun digitCheckValidation(): Boolean {
        return if (!profitLossInputValidation(sellPrice.value) || !profitLossInputValidation(
                profitProfitPercentage.value
            )
        ) {
            false
        } else if ((replaceString(sellPrice.value)?.length ?: 0) > 15) {
            false
        } else ((replaceString(profitProfitPercentage.value)?.toDouble())
            ?: 0.0) <= INPUT_FILTER_MAX_VALUE_PERCENTAGE.toDouble()


    }

    fun profitLossInputValidation(value: String?): Boolean {
        if (value?.isEmpty() == true) {
            return false
        }

        if (value?.length == 1 && Objects.equals(value, ".")) {

            return false
        }
        else if (Objects.equals(value, "-")) {
            return false
        }
        else if (Objects.equals(value, "0")) {
            return false
        }
        else if (Objects.equals(value, "0.00")) {
            return false
        }
        else if (Objects.equals(value, "0.0")) {
            return false
        }
        return true

    }

    fun replaceString(str:String?):String?{
        var replaceString=""
        context?.let {
            replaceString=
                str?.replace(it.getString(R.string.aoa), "")?.replace("$", "")?.replace(",", "")?.replace("%", "")?.trim()
                    .toString()
        }
        return replaceString
    }

    fun wrongInputMessage(){
        context?.let {
            MethodClass.showToastMsg(
                it,
                it.getString(R.string.please_enter_a_valid_value)
            )
        }
    }

}

