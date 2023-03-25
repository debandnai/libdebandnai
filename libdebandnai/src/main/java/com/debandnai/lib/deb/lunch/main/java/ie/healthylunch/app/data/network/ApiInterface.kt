package ie.healthylunch.app.data.network

import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.addNewStudent.AddNewStudent
import ie.healthylunch.app.data.model.allergenListModel.AllergenListResponse
import ie.healthylunch.app.data.model.caloremeterModel.CalorieMeterResponse
import ie.healthylunch.app.data.model.cardListModel.CardListResponse
import ie.healthylunch.app.data.model.changePasswordModel.ParentchangePasswordResponse
import ie.healthylunch.app.data.model.checkEmailModel.CheckemailExistResponse
import ie.healthylunch.app.data.model.clearOrderModel.CleareOrderResponse
import ie.healthylunch.app.data.model.countryModel.CountyResponse
import ie.healthylunch.app.data.model.couponCodeValidationModel.CouponCodeValidationResponse
import ie.healthylunch.app.data.model.dashBoardViewResponseModel.DashBoardViewResponse
import ie.healthylunch.app.data.model.dashboardBottomCalendar.DashboardBottomCalendarResponse
import ie.healthylunch.app.data.model.dashboardBottomCalendarNewModel.DashboardBottomCalendarNewResponse
import ie.healthylunch.app.data.model.dashboardCaloremeterSixDayNewModel.DasboardCalorieMeterSixDayNewResponse
import ie.healthylunch.app.data.model.deisStudentUniqueCodeModel.DeisStudentUniqueCodeResponse
import ie.healthylunch.app.data.model.deleteCardModel.DeleteCardResponse
import ie.healthylunch.app.data.model.deleteStudentModel.DeleteStudentResponse
import ie.healthylunch.app.data.model.deletenotificationModel.DeleteNotificationResponse
import ie.healthylunch.app.data.model.editParentEmailModel.EditParentEmail
import ie.healthylunch.app.data.model.editProfileModel.EditProfileResponse
import ie.healthylunch.app.data.model.editStudentModel.EditStudentResponse
import ie.healthylunch.app.data.model.favoriteOrdersAdd.FavoriteOrdersAddResponse
import ie.healthylunch.app.data.model.favorites.FavoritesResponse
import ie.healthylunch.app.data.model.favoritesRemoveModel.FavouriteRemoveResponse
import ie.healthylunch.app.data.model.feedBackListModel.FeedBackListResponse
import ie.healthylunch.app.data.model.getStudentAllowedOrderModel.StudentAllowedOrderResponse
import ie.healthylunch.app.data.model.holidayDeletedModel.holidaydeletedResponse
import ie.healthylunch.app.data.model.holidayResponse.CalendarListResponse
import ie.healthylunch.app.data.model.holidaySavedModel.holidaysavedResponse
import ie.healthylunch.app.data.model.initiatePaymentModel.InitiatePaymentResponse
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.logoutModel.LogoutResponse
import ie.healthylunch.app.data.model.menuTemplate.MenuTemplateResponse
import ie.healthylunch.app.data.model.notificationCountIncreaseModel.NotificationCountIncreaseResponse
import ie.healthylunch.app.data.model.notificationCountModel.NotificationCountResponse
import ie.healthylunch.app.data.model.notificationListPagingModel.NotificationListResponse
import ie.healthylunch.app.data.model.notificationSettingsModel.NotiFicationSettingsResponse
import ie.healthylunch.app.data.model.otpModel.OtpResponse
import ie.healthylunch.app.data.model.parentDetailsModel.ParentDetailsResponse
import ie.healthylunch.app.data.model.parentEmailVerificationModel.ParentemailverificationResponse
import ie.healthylunch.app.data.model.passwordResetModel.PasswordresetResponse
import ie.healthylunch.app.data.model.paymentProcessing.PaymentProcessingResponse
import ie.healthylunch.app.data.model.privacyPolicyModel.Privacypolicy
import ie.healthylunch.app.data.model.productInfoDetailsModel.ProductInfoDetailsResponse
import ie.healthylunch.app.data.model.productListByMenuTemplateModel.ProductListByMenuTemplateResponse
import ie.healthylunch.app.data.model.quickViewOrderDayModel.QuickViewOrderDayResponse
import ie.healthylunch.app.data.model.refreshTokenModel.RefreshTokenResponse
import ie.healthylunch.app.data.model.removeProductsHavingMayAllergenModel.RemoveProductsHavingMayAllergenResponse
import ie.healthylunch.app.data.model.replacedOrderForNextWeekSamedayModel.ReplacedOrderForNextWeekSamedayResponse
import ie.healthylunch.app.data.model.resendOtpModel.ResendOtpResponse
import ie.healthylunch.app.data.model.saveAllergenModel.SaveAllergensResponse
import ie.healthylunch.app.data.model.saveHolidayForSessionModel.saveHolidayforsessionResponse
import ie.healthylunch.app.data.model.saveOrder.SaveOrderResponse
import ie.healthylunch.app.data.model.schoolListModel.SchoolListResponse
import ie.healthylunch.app.data.model.sendFeedBackModel.SendFeedBackResponse
import ie.healthylunch.app.data.model.singleDayOrderDateDisplay.SingleDayOrderDateDisplayResponse
import ie.healthylunch.app.data.model.sixDayMenuListModel.SixDayMenuListResponse
import ie.healthylunch.app.data.model.studentAllergenCount.StudentAllergenCountResponse
import ie.healthylunch.app.data.model.studentClassModel.StudentClassResponse
import ie.healthylunch.app.data.model.studentDetailsModel.StudentDetailsResponse
import ie.healthylunch.app.data.model.studentListModel.StudenListResponse
import ie.healthylunch.app.data.model.transactionListPagingModel.TransactionListResponse
import ie.healthylunch.app.data.model.updateSettingsModel.NotificationSettingsUpdateResponse
import ie.healthylunch.app.data.model.updateStripeBalanceModel.UpdateStripeBalanceResponse
import ie.healthylunch.app.data.model.walletManualTopUpModel.WalletManualTopUpResponse
import ie.healthylunch.app.data.viewModel.userDeleteAccount.UserDeleteAccountResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/parentegistration")
    suspend fun parentRegistration(
        @Body jsonObject: JsonObject?,
        @Header("Content-Type") token_type: String
    ): LoginResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/checkemailExist")
    suspend fun checkEmailExistApi(
        @Body jsonObject: JsonObject?,
        @Header("Content-Type") token_type: String
    ): CheckemailExistResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("content/get_message")
    suspend fun parentRegistrationOtpAPI(
        @Body jsonObject: JsonObject?,
        @Header("Content-Type") token_type: String
    ): OtpResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("content/authenticatersetpasswordkey")
    suspend fun parentEmailVerificationAPI(
        @Body jsonObject: JsonObject?,
        @Header("Content-Type") token_type: String
    ): ParentemailverificationResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/resendotpforemailverification")
    suspend fun resendOtpResponseAPI(
        @Body jsonObject: JsonObject?,
        @Header("Content-Type") token_type: String
    ): ResendOtpResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/updateUserEmail")
    suspend fun editParentEmailApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): EditParentEmail

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/getCountiesData")
    suspend fun countryApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): CountyResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("content/resetpassword")
    suspend fun resetPasswordApi(
        @Body jsonObject: JsonObject?,
        @Header("Content-Type") token_type: String
    ): PasswordresetResponse


    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("student/favouriteOrdersAdd")
    suspend fun favouriteOrdersAddApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): FavoriteOrdersAddResponse

   @Headers("Content-Type: application/json; charset=utf-8")
    @POST("student/favouriteOrderRemove")
    suspend fun favouriteOrdersRemoveApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): FavouriteRemoveResponse


    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/activeSchoolListbyCountyid")
    suspend fun schoolListApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): SchoolListResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/classListbySchoolid")
    suspend fun studentClassApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): StudentClassResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/addStudent")
    suspend fun addNewStudentApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): AddNewStudent

    @Headers("Content-Type: application/json; charset=utf-8")
    //@POST("allergen/allergenList")
    @POST("allergen/allergen-list-by-category")
    suspend fun allergenListApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): AllergenListResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("allergen/saveAllergen")
    suspend fun saveAllergensApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): SaveAllergensResponse


    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("authenticate")
    suspend fun loginResponseAPI(
        @Body jsonObject: JsonObject?,
        @Header("Content-Type") token_type: String
    ): LoginResponse


    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/studentlist")
    suspend fun studentListApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): StudenListResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("student/favouriteOrdersList")
    suspend fun favoritesListApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): FavoritesResponse



    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/studentdetails")
    suspend fun studentDetailsApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): StudentDetailsResponse


    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/notificationcount")
    suspend fun notificationCountResponseApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): NotificationCountResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/parentDetails")
    suspend fun parentDetailsResponseApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): ParentDetailsResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("content/footer")
    suspend fun privacyPolicyResponseApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): Privacypolicy

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/content/userLogout")
    suspend fun logoutApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): LogoutResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/parentChangepassword")
    suspend fun parentChangePasswordResponse(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): ParentchangePasswordResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/parentEditprofile")
    suspend fun editProfileApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): EditProfileResponse

    /*@Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/notificationlist")
    suspend fun notificationListApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): NotificationListResponse*/

    @POST("parent/notificationlist")
    suspend fun notificationListPagingApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String
    ): NotificationListResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/editStudent")
    suspend fun editStudentResponseApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): EditStudentResponse


    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/deleteNotification")
    suspend fun deleteNotificationApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): DeleteNotificationResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/listNotificationSettings")
    suspend fun notificationSettingsApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String
    ): NotiFicationSettingsResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/updateNotification")
    suspend fun notificationSettingsUpdateApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): NotificationSettingsUpdateResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("student/dashBoardMenuListSixDayNew")
    suspend fun sixDayMenuListResponseApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): SixDayMenuListResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("student/dashboardCaloriMeterNew")
    suspend fun dashBoardBottomCalenderResponseApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): DashboardBottomCalendarResponse

    @POST("student/replacedOrderForNextWeekSameday")
    suspend fun replacedOrderForNextWeekSameDayApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): ReplacedOrderForNextWeekSamedayResponse


    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("student/menutemplateList")
    suspend fun menuTemplateResponseApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): MenuTemplateResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/quickViewOrderDay")
    suspend fun quickViewResponseApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): QuickViewOrderDayResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("parent/quickViewOrder")
    suspend fun quickViewOrderResponseApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String
    ): QuickViewOrderDayResponse


    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("student/productLitByMenuTemplate")
    suspend fun productListByMenuTemplateResponseApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): ProductListByMenuTemplateResponse

   /* @Headers("Content-Type: application/json; charset=utf-8")
    @POST("student/savemenuForSingleDay")
    suspend fun saveMenuForSingleDayResponseApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): SaveMenuForSingleDayResponse*/

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("order/saveOrder")
    suspend fun saveOrderResponseApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): SaveOrderResponse



    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("student/caloremeter")
    suspend fun calorieMeterResponseApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): CalorieMeterResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("student/singledayOrderDateDisplay")
    suspend fun singleDayOrderDateDisplayResponseApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): SingleDayOrderDateDisplayResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("student/getStudentallowedOrder")
    suspend fun studentAllowedOrderResponseApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): StudentAllowedOrderResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("order/paymentProcessing")
    suspend fun paymentProcessingResponseApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): PaymentProcessingResponse


    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("student/clearOrderForParticularDay")
    suspend fun clearOrderResponseApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): CleareOrderResponse


    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("calender/holidayList")
    suspend fun holidayListApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): CalendarListResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("calender/deleteHoliday")
    suspend fun holidayDeleteApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): holidaydeletedResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("calender/saveHoliday")
    suspend fun holidaySaveApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): holidaysavedResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("calender/saveHolidayforsession")
    suspend fun holidaySaveSessionApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): saveHolidayforsessionResponse

    @Headers("Content-Type: application/json; charset=utf-8")

    @POST("student/feedbackList")
    suspend fun feedBackListApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): FeedBackListResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("student/studentSendFeedback")
    suspend fun sendFeedBackApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): SendFeedBackResponse

    @POST("parent/deleteStudent")
    suspend fun deleteStudentApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): DeleteStudentResponse

    @POST("parent/cardlist")
    suspend fun cardListApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): CardListResponse

    @POST("parent/deletecard")
    suspend fun deleteCardApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): DeleteCardResponse

    @POST("parent/walletManualTopUp")
    suspend fun walletManualTopUpApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): WalletManualTopUpResponse

    @POST("parent/initiatePaymentIntent")
    suspend fun initiatePaymentIntentApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): InitiatePaymentResponse

    @POST("parent/updateStripeBalance")
    suspend fun updateStripeBalanceApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): UpdateStripeBalanceResponse

    @POST("parent/transuctionlist")
    suspend fun transactionListApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): TransactionListResponse

    @POST("parent/transuctionlist")
    suspend fun transactionListPagingApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String
    ): TransactionListResponse

    @POST("parent/couponCodeValidationNew")
    suspend fun couponCodeValidationApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): CouponCodeValidationResponse

    @POST("student/removeproductmayallergen")
    suspend fun removeProductMayAllergenApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): RemoveProductsHavingMayAllergenResponse

    @POST("student/dashboardCaloremeterSixDayNew")
    suspend fun dashboardCalorieMeterSixDayNewApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): DasboardCalorieMeterSixDayNewResponse

    @POST("student/viewLoyalty")
    suspend fun dashboardBottomCalendarNewApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): DashboardBottomCalendarNewResponse

    @POST("regenerate-token")
    suspend fun refreshTokenApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): RefreshTokenResponse

    @POST("order/viewDashboard")
    suspend fun dashBoardViewApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): DashBoardViewResponse

    @POST("order/viewDashboardCurrentOrder")
    suspend fun viewDashboardCurrentOrder(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): DashBoardViewResponse

    @POST("order/i-button-details")
    suspend fun productInfoDetailsApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): ProductInfoDetailsResponse

    @POST("parent/addDeisStudent")
    suspend fun deisStudentAddApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): DeisStudentUniqueCodeResponse

    @POST("increse-click-count-for-push-notifications")
    suspend fun pushNotificationCountIncrease(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): NotificationCountIncreaseResponse

    @POST("parent/userDeactiveAccount")
    suspend fun userDeleteAccount(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String,
        @Header("Content-Type") token_type: String
    ): UserDeleteAccountResponse

    @POST("allergen/allergenCount")
    suspend fun studentAllergenCountApi(
        @Body jsonObject: JsonObject?,
        @Header("Authorization") token: String
    ): StudentAllergenCountResponse

}

