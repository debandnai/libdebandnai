package com.merkaaz.app.ui.dialogs

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.merkaaz.app.R
import com.merkaaz.app.data.model.ProfitlossProduct
import com.merkaaz.app.data.viewModel.CalculatorBottomSheetViewModel
import com.merkaaz.app.databinding.ManagedProfitLossBottomSheetDialogBinding
import com.merkaaz.app.interfaces.CalculateClickListener
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.Constants.INPUT_FILTER_MAX_VALUE_PERCENTAGE
import com.merkaaz.app.utils.DecimalDigitsInputFilterProfitLoss
import com.merkaaz.app.utils.MethodClass
import com.merkaaz.app.utils.MethodClass.hideSoftKeyboard
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


@AndroidEntryPoint
class ManageProfitLossBottomSheet(
    val profitLossList: ProfitlossProduct,
    private val calculateClickListener: CalculateClickListener
) : BottomSheetDialogFragment() {
    val TAG:String="ManageProfitLossBottomSheet"
    var textWatcherSellPrice: TextWatcher? = null
    var textWatcherProfit: TextWatcher? = null
    lateinit var binding: ManagedProfitLossBottomSheetDialogBinding
    private lateinit var calculatorBottomSheetViewModel: CalculatorBottomSheetViewModel
    var numberFormat: NumberFormat?= null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.managed_profit_loss_bottom_sheet_dialog,
            container,
            false
        )
      //  dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        calculatorBottomSheetViewModel =
            ViewModelProvider(this)[CalculatorBottomSheetViewModel::class.java]
        binding.viewmodel = calculatorBottomSheetViewModel
        binding.context = this
        binding.lifecycleOwner = this
        initialise()
        textChangeListener()
        onViewClick()
        return binding.root
    }

    private fun onViewClick() {
        activity?.let { hideSoftKeyboard(it)}
        binding.btnCalculateAndUpdate.setOnClickListener {
            activity?.let { hideSoftKeyboard(it)}
            if(calculatorBottomSheetViewModel.digitCheckValidation()) {
                calculateClickListener.calculateAndUpdate(
                    profitLossList.id.toString(),
                    calculatorBottomSheetViewModel.replaceString(calculatorBottomSheetViewModel.sellPrice.value),
                    calculatorBottomSheetViewModel.replaceString(calculatorBottomSheetViewModel.profitProfitPercentage.value),
                    Constants.CALCULATE
                )
            }
            else {
               // calculatorBottomSheetViewModel.wrongInputMessage()
                context?.let {
                    MethodClass.showToastMsg(
                        it,
                        it.getString(R.string.please_enter_a_valid_value)
                    )
                }
            }

        }
        binding.clHeader.setOnClickListener {
            //activity?.let { hideSoftKeyboard(it)}
            hideKeyBoard()
        }
    }


    private fun initialise() {

        numberFormatForEnglish()
        binding.profitlossProduct = profitLossList
        binding.etSellPrice.filters = arrayOf<InputFilter>(DecimalDigitsInputFilterProfitLoss(14,2))
        binding.etProfit.filters = arrayOf<InputFilter>(DecimalDigitsInputFilterProfitLoss(4,2))

        //If sell price is 0 then set cost price
        //First time costPrice and sellPrice with , from API. So it need to replace
        if (Objects.equals(calculatorBottomSheetViewModel.replaceString(profitLossList.sellPrice),"0.00"))
            calculatorBottomSheetViewModel.sellPrice.value = currencyFormat(calculatorBottomSheetViewModel.replaceString(numberFormat?.format(calculatorBottomSheetViewModel.replaceString(profitLossList.costPrice)?.toDouble())))
        else
            calculatorBottomSheetViewModel.sellPrice.value = currencyFormat(calculatorBottomSheetViewModel.replaceString(numberFormat?.format(calculatorBottomSheetViewModel.replaceString(profitLossList.sellPrice)?.toDouble())))
        calculatorBottomSheetViewModel.profitProfitPercentage.value =currencyFormat(calculatorBottomSheetViewModel.replaceString(numberFormat?.format(calculatorBottomSheetViewModel.replaceString(profitLossList.profitPercent)?.toDouble())))
    }

    private fun textChangeListener() {

        // TextWatcher for profit percentage
        textWatcherProfit = object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (validation(s) )
                setSellPrice(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        }
        // TextWatcher for sell price
        textWatcherSellPrice = object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (validation(s) )
                setProfit(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        }


        binding.etSellPrice.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    binding.etSellPrice.addTextChangedListener(textWatcherSellPrice)
                    binding.etProfit.removeTextChangedListener(textWatcherProfit)
                } else {
                    activity?.let { hideSoftKeyboard(it)}
                    binding.etSellPrice.removeTextChangedListener(textWatcherSellPrice)
                }
            }
        binding.etProfit.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    binding.etSellPrice.removeTextChangedListener(textWatcherSellPrice)
                    binding.etProfit.addTextChangedListener(textWatcherProfit)
                } else {
                    activity?.let { hideSoftKeyboard(it)}
                    binding.etProfit.removeTextChangedListener(textWatcherProfit)
                }
            }

        binding.etSellPrice.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.btnCalculateAndUpdate.performClick()
            }
            false
        })


        binding.etProfit.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.btnCalculateAndUpdate.performClick()
            }
            false
        })

    }

    private fun setSellPrice(strProfitProfitPercentage: String) {
        if (isNumeric(strProfitProfitPercentage)){
            var profit: Double = 0.0
            var profitPercentage: Double = 0.0

            /*
        sample demo for Selling price
        C.P = Rs 15000
        Profit % = 20%
        Profit = C.P x profit %
        Profit = 15000 X 20/100
        Profit = Rs 3,000
        Selling price = Profit + C.P

         */

            Log.d(TAG, "setSellPrice: $strProfitProfitPercentage")
            if (strProfitProfitPercentage != "-") {
                val cost: Double = calculatorBottomSheetViewModel.replaceString(profitLossList.costPrice)?.toDouble() ?: 0.0
                profitPercentage = calculatorBottomSheetViewModel.replaceString(strProfitProfitPercentage)?.toDouble() ?: 0.0
                profit = (cost * profitPercentage) / 100

                val sellPrice = (profit + cost).toString()
                calculatorBottomSheetViewModel.sellPrice.value =
                    currencyFormat(calculatorBottomSheetViewModel.replaceString(numberFormat?.format(calculatorBottomSheetViewModel.replaceString(sellPrice)?.toDouble())))
            }
        }

    }

    private fun setProfit(strSellPrice: String) {
        val costPrice: Double =calculatorBottomSheetViewModel.replaceString(profitLossList.costPrice)?.toDouble()?: 0.0
        if(isNumeric(strSellPrice)){
            if (calculatorBottomSheetViewModel.replaceString(strSellPrice)?.toDouble() != costPrice){
            var sellPrice: Double = 0.0

            sellPrice = calculatorBottomSheetViewModel.replaceString(strSellPrice)?.toDouble()?: 0.0
            val profit = sellPrice - costPrice
            if (((profit * 100) / costPrice)<0)
                binding.tvSpLCp.visibility=View.VISIBLE
            else
                binding.tvSpLCp.visibility=View.GONE



            var profitProfitPercentage=((profit * 100) / costPrice)
            if (profitProfitPercentage>INPUT_FILTER_MAX_VALUE_PERCENTAGE) {
                profitProfitPercentage = 0.0
                context?.let { MethodClass.custom_msg_dialog(it,it.getString(R.string.profit_percentage_can_t_be_greater_than_10000))?.show()}
            }
                calculatorBottomSheetViewModel.profitProfitPercentage.value = currencyFormat(calculatorBottomSheetViewModel.replaceString(numberFormat?.format(profitProfitPercentage)))
        }
        }
    }
    private fun isNumeric(str: String): Boolean {
        return try {
            calculatorBottomSheetViewModel.replaceString(str)?.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }


    //For format currency pattern
    private fun currencyFormat(amount: String?): String? {
     if (amount?.isNotEmpty() == true) {
            val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
            formatter.applyPattern("###,###,##0.00")
            return formatter.format(amount?.toDouble())
        }
        return ""
    }

    override fun getTheme(): Int {
        return R.style.CalculatorSheetDialog
    }

    private fun validation(s: Editable):Boolean{
         if (s.isNullOrEmpty())
          return  false

        return true
    }
    private fun numberFormatForEnglish(){
        numberFormat = NumberFormat.getCurrencyInstance(Locale("en", "US"))
    }

    private fun hideKeyBoard() {
        context?.let {
            val imm = it.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.btnCalculateAndUpdate.windowToken, 0)
        }

    }
}
