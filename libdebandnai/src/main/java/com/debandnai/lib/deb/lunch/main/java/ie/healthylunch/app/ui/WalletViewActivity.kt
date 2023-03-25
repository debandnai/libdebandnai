package ie.healthylunch.app.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.ParentViewWalletPageOneActivityRepository
import ie.healthylunch.app.data.viewModel.ParentViewWalletPageOneActivityViewModel
import ie.healthylunch.app.databinding.ActivityWalletViewBinding
import ie.healthylunch.app.databinding.ClearOrderItemDialogBinding
import ie.healthylunch.app.databinding.WalletTopupAlertDialogBinding
import ie.healthylunch.app.ui.base.BaseActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.ZERO_POINT_ZERO
import java.util.*

class WalletViewActivity :
    BaseActivity<ParentViewWalletPageOneActivityViewModel, ParentViewWalletPageOneActivityRepository>()
, DialogYesNoListener{
    private lateinit var loader: Dialog
    private lateinit var binding: ActivityWalletViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        window.statusBarColor = ContextCompat.getColor(this@WalletViewActivity, R.color.sky_bg_2)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_wallet_view
        )

        binding.activity = this@WalletViewActivity
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
//        viewModel.activity = this
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

        init()
    }

    override fun getViewModel() = ParentViewWalletPageOneActivityViewModel::class.java
    override fun getRepository() =
        ParentViewWalletPageOneActivityRepository(remoteDataSource.buildApi(ApiInterface::class.java))

    private fun init(){
        getIntentValue()
        viewModel.walletBalance.value = getString(R.string.euro, ZERO_POINT_ZERO)
        viewModel.cardNumber.value = ""
        viewModel.isCardAdded.value = false
        viewModel.brandName.value = ""
        loader = MethodClass.loaderDialog(binding.root.context)
    }

    private fun getIntentValue() {
        //get data from intent
        if (intent.extras != null) {
            if (!intent.getStringExtra(Constants.FROM).isNullOrBlank())
                viewModel.from=intent.getStringExtra(Constants.FROM)
        }
    }

    private fun getToken() : String? {
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this@WalletViewActivity,
                Constants.USER_DETAILS
            )
        return loginResponse?.response?.raws?.data?.token
    }


    fun cardListApiCall() {
        loader.show()
        getToken()?.let { token ->
            val cardDetailsJsonObject = MethodClass.getCommonJsonObject(this@WalletViewActivity)
            viewModel.cardList(cardDetailsJsonObject, token)
            cardListApiResponse()
        }
    }

    //delete card api call

    fun deleteCardApiCall() {
        getToken()?.let { token ->
            val jsonObject = MethodClass.getCommonJsonObject(this@WalletViewActivity)
            viewModel.deleteCard(jsonObject, token)
            deleteCardResponse()
        }
    }

    fun deleteCardClick() {
        CustomDialog.showYesNoTypeDialog(
            this@WalletViewActivity,
            getString(R.string.do_you_want_to_delete_this_card_),
            this@WalletViewActivity
        )
    }

    //Dialog override functions

    override fun yesOnClick(dialog: Dialog, activity: Activity) {
        dialog.dismiss()
        deleteCardApiCall()
    }

    override fun noOnClick(dialog: Dialog, activity: Activity) {
        dialog.dismiss()
    }

    //click functions
    fun topUpVoucher() {
        startActivity(Intent(this@WalletViewActivity, ParentTopUpByVoucherActivity::class.java))
    }

    fun transaction() {
        startActivity(Intent(this@WalletViewActivity, ListAllWalletTransactionActivity::class.java))
    }

    fun addCard() {
        val intent=Intent(this@WalletViewActivity, AddNewCardActivity::class.java)
        viewModel.from?.let { intent.putExtra(Constants.FROM,it) }
        startActivity(intent)
    }

    fun topUpLater() {
        if (viewModel.isCardAdded.value == true) {
            val intent = Intent(
                this@WalletViewActivity,
                ParentTopUpNowActivity::class.java
            )
            intent.putExtra("card_number", viewModel.cardNumber.value)
            intent.putExtra("brand", viewModel.brandName.value)
            startActivity(intent)
        } else {
            walletTopUpDialog()
        }
    }

    private fun walletTopUpDialog(){
        val dialog = Dialog(this@WalletViewActivity)

        dialog.window?.let { window ->
            window.setBackgroundDrawableResource(R.color.transparent)
            window.decorView.setBackgroundResource(android.R.color.transparent)
            window.setDimAmount(0.0f)
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val dialogBinding =
            WalletTopupAlertDialogBinding.inflate(LayoutInflater.from(this@WalletViewActivity))
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)

        with(dialogBinding){
            btnTopUpNow.setOnClickListener {
                dialog.dismiss()
                addCard()
            }
            btnTopUpLater.setOnClickListener {
                val intent = Intent(
                    this@WalletViewActivity,
                    DashBoardActivity::class.java
                )
                dialog.dismiss()
                startActivity(intent)
                finish()
            }
        }
        dialog.show()
    }


    private fun cardListApiResponse() {
        //MethodClass.showProgressDialog(this@WalletViewActivity)
        viewModel.cardListResponse?.observe(this@WalletViewActivity) {

            when (it) {
                is Resource.Success -> {
                    loader.dismiss()
                    val data = it.value.response.raws.data

                    viewModel.walletBalance.value = getString(R.string.euro, data.walletBalance)

                    //check if card added or not
                    viewModel.isCardAdded.value = (!Objects.equals(
                        data.cardnumber,
                        ""
                    ) && !Objects.equals(
                        data.cardnumber,
                        "null"
                    ) && !Objects.equals(data.cardnumber, null))


                    //set card number depending on isCardAdded
                    if (viewModel.isCardAdded.value!!)
                        viewModel.cardNumber.value = "xxxx xxxx xxxx ${data.cardnumber}"
                    else
                        viewModel.cardNumber.value = ""

                    //get brand name
                    viewModel.brandName.value = data.brand

                    //MethodClass.hideProgressDialog(this@WalletViewActivity)
                }
                is Resource.Failure -> {
                    loader.dismiss()
                    //Log.d("CardDetails", "Not Ok")
                    if (it.errorBody != null) {
                        it.errorString?.let { _ ->

                            if (it.errorCode == 401)
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        this@WalletViewActivity, null,
                                        Constants.CARD_DETAILS,
                                        Constants.REFRESH_TOKEN
                                    )
                            else
                                it.errorString.let { it1 ->

                                    MethodClass.showErrorDialog(
                                        this@WalletViewActivity,
                                        it1,
                                        it.errorCode
                                    )
                                }
                        }

                    }
                    //MethodClass.hideProgressDialog(this@WalletViewActivity)
                    viewModel._cardListResponse.value = null
                    viewModel.cardListResponse = viewModel._cardListResponse
                }
                else -> {
                    loader.dismiss()
                }
            }

        }
    }

    //delete card response

    private fun deleteCardResponse() {
        MethodClass.showProgressDialog(this@WalletViewActivity)
        viewModel.deleteCardResponse?.observe(this@WalletViewActivity) {

            when (it) {
                is Resource.Success -> {
                    MethodClass.hideProgressDialog(this@WalletViewActivity)
                    Toast.makeText(
                        this@WalletViewActivity,
                        it.value.response.raws.successMessage,
                        Toast.LENGTH_SHORT
                    ).show()

                    viewModel._deleteCardResponse.value = null
                    viewModel.deleteCardResponse = viewModel._deleteCardResponse
                    cardListApiCall()
                }
                is Resource.Failure -> {
                    Log.d("CardDetails", "Not Ok")
                    if (it.errorBody != null) {
                        it.errorString?.let { _ ->

                            if (it.errorCode == 401)
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        this@WalletViewActivity, null,
                                        Constants.DELETE_CARD,
                                        Constants.REFRESH_TOKEN
                                    )
                            else
                                it.errorString.let { it1 ->

                                    MethodClass.showErrorDialog(
                                        this@WalletViewActivity,
                                        it1,
                                        it.errorCode
                                    )
                                }
                        }

                    }
                    MethodClass.hideProgressDialog(this@WalletViewActivity)
                    viewModel._deleteCardResponse.value = null
                    viewModel.deleteCardResponse = viewModel._deleteCardResponse
                }
                else -> {}
            }

        }
    }


    override fun onResume() {
        super.onResume()
        cardListApiCall()
    }

    private val onBackPressedCallback=object:OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
                val intent=Intent(this@WalletViewActivity, DashBoardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
        }

    }
}