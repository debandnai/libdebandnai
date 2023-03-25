package com.merkaaz.app.ui.dialogs

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.merkaaz.app.R
import com.merkaaz.app.data.model.ProfitlossProduct
import com.merkaaz.app.data.viewModel.CalculatorBottomSheetViewModel
import com.merkaaz.app.databinding.BottomSheetCalculatePopupBinding
import com.merkaaz.app.interfaces.CalculateClickListener
import com.merkaaz.app.utils.Constants.CALCULATE
import com.merkaaz.app.utils.Constants.INPUT_FILTER_MAX_VALUE
import com.merkaaz.app.utils.Constants.INPUT_FILTER_MAX_VALUE_PERCENTAGE
import com.merkaaz.app.utils.Constants.INPUT_FILTER_POINTER_LENGTH
import com.merkaaz.app.utils.DecimalDigitsInputFilter
import com.merkaaz.app.utils.MethodClass
import com.merkaaz.app.utils.MethodClass.showToastMsg
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


@AndroidEntryPoint
class CalculateBottomSheet(
    val profitLossList: ProfitlossProduct,
    private val calculateClickListener: CalculateClickListener
) : BottomSheetDialogFragment() {
    private var mPattern: Pattern? = null
    var textWatcherSellPrice: TextWatcher? = null
    var textWatcherProfit: TextWatcher? = null
    lateinit var binding: BottomSheetCalculatePopupBinding
    private lateinit var calculatorBottomSheetViewModel: CalculatorBottomSheetViewModel
    var numberFormat: NumberFormat ?= null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_calculate_popup,
            container,
            false
        )
        calculatorBottomSheetViewModel =
            ViewModelProvider(this)[CalculatorBottomSheetViewModel::class.java]
        binding.viewmodel = calculatorBottomSheetViewModel
        binding.context = this
        binding.lifecycleOwner = this
        initialise()
        onViewClick()
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.SheetDialog
    }

    private fun initialise() {
        numberFormatForEnglish()
        mPattern = Pattern.compile("^-?\\d+(\\.\\d{2})?\$")
        binding.profitlossProduct = profitLossList




        //If sell price is 0 then set cost price
        if (Objects.equals(profitLossList.sellPrice?.replace(getString(R.string.aoa), "")?.replace(",", "")?.trim(),"0.00"))
            calculatorBottomSheetViewModel.sellPrice.value = profitLossList.costPrice?.replace(getString(R.string.aoa), "")?.replace(",", "")?.trim()
        else
            calculatorBottomSheetViewModel.sellPrice.value = profitLossList.sellPrice?.replace(getString(R.string.aoa), "")?.replace(",", "")?.trim()
        calculatorBottomSheetViewModel.profitProfitPercentage.value =profitLossList.profitPercent?.replace(getString(R.string.aoa), "")?.replace(",", "")?.replace("%", "")?.trim()
        textChangeListener()


        //initialise time check is percentage is negative or not
        if (calculatorBottomSheetViewModel.profitProfitPercentage.value?.contains("-") == true)
            binding.tvSpLCp.visibility=View.VISIBLE
        else
            binding.tvSpLCp.visibility=View.GONE

    }

    private fun onViewClick() {
        binding.btnCalculateAndUpdate.setOnClickListener {
            hideKeyBoard()
            calculatorBottomSheetViewModel.sellPrice.value =currencyFormat(calculatorBottomSheetViewModel.sellPrice.value)?.replace(",",".")
            calculatorBottomSheetViewModel.profitProfitPercentage.value =currencyFormat(calculatorBottomSheetViewModel.profitProfitPercentage.value)?.replace(",",".")
            // if(calculatorBottomSheetViewModel.profitProfitPercentage.value?.isNotEmpty() == true) {
            /*val matcher: Matcher? = mPattern?.matcher(calculatorBottomSheetViewModel.sellPrice.value)
            if (mPattern?.matcher())*/
            // if (patternValidation(calculatorBottomSheetViewModel.sellPrice.value) && patternValidation(calculatorBottomSheetViewModel.profitProfitPercentage.value)){
            //  if (currencyValidation()){
                if(calculatorBottomSheetViewModel.digitCheckValidation()) {
                    calculateClickListener.calculateAndUpdate(
                        profitLossList.id.toString(),
                        calculatorBottomSheetViewModel.sellPrice.value,
                        calculatorBottomSheetViewModel.profitProfitPercentage.value,
                        CALCULATE
                    )
                } else{
                context?.let {  showToastMsg(it,getString(R.string.sell_price_or_profit_percentage_can_t_be_blank_or_0)) }
            }
            //}
            /*else{
                //context?.let { showToastMsg(it,"Invalid Input")  }
                calculatorBottomSheetViewModel.sellPrice.value =currencyFormat(calculatorBottomSheetViewModel.sellPrice.value)
                calculatorBottomSheetViewModel.profitProfitPercentage.value = currencyFormat(calculatorBottomSheetViewModel.profitProfitPercentage.value)

                //Set cursor last position in edittext
                setCursorLastPosition(calculatorBottomSheetViewModel.sellPrice.value,isSellPriceEditTextFocus)
                setCursorLastPosition(calculatorBottomSheetViewModel.profitProfitPercentage.value,isSellPriceEditTextFocus)
            }*/


            /*}
            else{
                context?.let { MethodClass.showToastMsg(it,getString(R.string.please_enter_profit_percentage)) }

            }*/
        }
        binding.clHeader.setOnClickListener {
            hideKeyBoard()
        }
    }



    /*private fun calculateSubmitValidation(): Boolean {
       if (calculatorBottomSheetViewModel.sellPrice.value?.isNotBlank())

        return true
    }*/


    private fun textChangeListener() {

        // TextWatcher for profit percentage
        textWatcherProfit = object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                setSellPrice()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        }
        // TextWatcher for sell price
        textWatcherSellPrice = object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                setProfit()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        }
        // Check 2 digit after decimal
        binding.etSellPrice.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(INPUT_FILTER_MAX_VALUE, INPUT_FILTER_POINTER_LENGTH))
        binding.etProfit.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(INPUT_FILTER_MAX_VALUE_PERCENTAGE, INPUT_FILTER_POINTER_LENGTH))

        binding.etSellPrice.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    binding.etSellPrice.addTextChangedListener(textWatcherSellPrice)
                    binding.etProfit.removeTextChangedListener(textWatcherProfit)
                } else {
                    hideKeyBoard()
                    binding.etSellPrice.removeTextChangedListener(textWatcherSellPrice)
                }
            }
        binding.etProfit.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    binding.etSellPrice.removeTextChangedListener(textWatcherSellPrice)
                    binding.etProfit.addTextChangedListener(textWatcherProfit)
                } else {
                    hideKeyBoard()
                    binding.etProfit.removeTextChangedListener(textWatcherProfit)
                }
            }


        binding.etSellPrice.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == IME_ACTION_DONE) {
                binding.btnCalculateAndUpdate.performClick()
            }
            false
        })


        binding.etProfit.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == IME_ACTION_DONE) {
                binding.btnCalculateAndUpdate.performClick()
            }
            false
        })

    }


    private fun setSellPrice() {
        var profit: Double = 0.0
        var profitPercentage: Double = 0.0
        // var sellPrice = ""

        /*
        sample demo for Selling price
        C.P = Rs 15000
        Profit % = 20%
        Profit = C.P x profit %
        Profit = 15000 X 20/100
        Profit = Rs 3,000
        Selling price = Profit + C.P

         */

        if (!calculatorBottomSheetViewModel.profitLossInputValidation(calculatorBottomSheetViewModel.profitProfitPercentage.value)) {
            calculatorBottomSheetViewModel.profitProfitPercentage.value = ""
            calculatorBottomSheetViewModel.sellPrice.value = ""
            binding.tvSpLCp.visibility=View.GONE
            return
        }

        val cost: Double =profitLossList.costPrice?.replace("AOA", "")?.replace(",", "")?.trim()?.toDouble()?: 0.0
        profitPercentage = calculatorBottomSheetViewModel.profitProfitPercentage.value?.replace("AOA", "")?.replace(",", "")?.trim()?.toDouble() ?: 0.0
        profit = (cost * profitPercentage) / 100

        //decimalFormat()
        var sellPrice=numberFormat?.format((profit + cost))
        calculatorBottomSheetViewModel.sellPrice.value =currencyFormat(sellPrice)?.replace("AOA", "")?.replace(",", ".")?.trim()

    }


    private fun setProfit() {
        // var profitValue = ""
        var sellPrice: Double = 0.0

        if (!calculatorBottomSheetViewModel.profitLossInputValidation(calculatorBottomSheetViewModel.sellPrice.value)) {
            calculatorBottomSheetViewModel.sellPrice.value = ""
            calculatorBottomSheetViewModel.profitProfitPercentage.value = ""
            binding.tvSpLCp.visibility=View.GONE
            return
        }
        val cost: Double =profitLossList.costPrice?.replace("AOA", "")?.replace(",", "")?.trim()?.toDouble()?: 0.0
        sellPrice = calculatorBottomSheetViewModel.sellPrice.value?.replace("AOA", "")?.replace(",", "")?.trim()?.toDouble()?: 0.0
        val profit = sellPrice - cost
        if (checkNegativeValue(((profit * 100) / cost)))
            binding.tvSpLCp.visibility=View.VISIBLE
        else
            binding.tvSpLCp.visibility=View.GONE


        var profitProfitPercentage=numberFormat?.format(((profit * 100) / cost))
        calculatorBottomSheetViewModel.profitProfitPercentage.value =currencyFormat(profitProfitPercentage)?.replace("AOA", "")?.replace(",", ".")?.trim()

    }

    private fun checkNegativeValue(d: Double): Boolean {
        return d<0
    }


    //For check currency pattern
    private fun currencyValidation(): Boolean {
        if (!patternValidation(calculatorBottomSheetViewModel.sellPrice.value)) {
         //   calculatorBottomSheetViewModel.sellPrice.value =currencyFormat(calculatorBottomSheetViewModel.sellPrice.value)
            return false
        }
        else if (!patternValidation(calculatorBottomSheetViewModel.profitProfitPercentage.value)) {
          //  calculatorBottomSheetViewModel.profitProfitPercentage.value =currencyFormat(calculatorBottomSheetViewModel.profitProfitPercentage.value)
            return false
        }
        return true
    }
    //For check currency pattern
    private fun patternValidation(value: String?): Boolean {
        val matcher: Matcher? = value?.let { mPattern?.matcher(it) }
        return matcher?.matches() == true
    }

    //For format currency pattern
    private fun currencyFormat(amount: String?): String? {
        if (amount?.isNotEmpty() == true) {
              val formatter = DecimalFormat("########0.00")
              return formatter.format(amount.replace(",","").toDouble())
        }
        return ""
    }


    private fun hideKeyBoard() {
        context?.let {
            val imm = it.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.btnCalculateAndUpdate.windowToken, 0)
        }

    }

    private fun numberFormatForEnglish(){
        numberFormat = NumberFormat.getInstance(Locale("en", "US"))
    }
}
