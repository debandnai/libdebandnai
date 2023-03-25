package com.salonsolution.app.data.network

import android.content.Context
import com.salonsolution.app.R

object ResponseMessageConverter {

    private const val PLEASE_PROVIDE_VALID_USERNAME = "Please provide valid username."
    private const val PLEASE_PROVIDE_VALID_PASSWORD = "Please provide valid password."
    private const val YOU_ARE_NOT_A_REGISTER_USER = "You are not a registered user."
    private const val YOUR_ACCOUNT_HAS_BEEN_DEACTIVATE = "Your account has been deactivated"
    private const val WRONG_CREDENTIAL = "Wrong credential"

    fun getLoginErrorMessage(context: Context, msg: String?): String {
        return when {
            msg.equals(PLEASE_PROVIDE_VALID_USERNAME, true) -> {
                context.getString(R.string.please_provide_valid_username)
            }
            msg.equals(PLEASE_PROVIDE_VALID_PASSWORD, true) -> {
                context.getString(R.string.please_provide_valid_password)
            }
            msg.equals(YOU_ARE_NOT_A_REGISTER_USER, true) -> {
                context.getString(R.string.you_are_not_a_registerd_user)
            }
            msg.equals(YOUR_ACCOUNT_HAS_BEEN_DEACTIVATE, true) -> {
                context.getString(R.string.your_account_has_been_deactivated)
            }
            msg.equals(WRONG_CREDENTIAL, true) -> {
                context.getString(R.string.wrong_credential)
            }
            else -> {
                context.getString(R.string.something_went_wrong)
            }
        }
    }

    private const val PLEASE_PROVIDE_VALID_CUSTOMER_FIRST_NAME = "Please provide a valid customer first name."
    private const val PLEASE_PROVIDE_VALID_CUSTOMER_LAST_NAME = "Please provide a valid customer last name."
    private const val PLEASE_PROVIDE_VALID_COUNTRY_CODE = "Please provide a valid country code."
    private const val PLEASE_PROVIDE_VALID_PHONE_NUMBER = "Please provide a valid phone number."
    private const val PLEASE_PROVIDE_VALID_EMAIL_ID = "Please provide a valid email id."
    private const val EMAIL_ID_ALREADY_EXISTS = "Email id already exists."
    private const val PHONE_NUMBER_ALREADY_EXISTS = "Phone number already exists."


    fun getRegisterErrorMessage(context: Context, msg: String?): String {
        return when {
            msg.equals(PLEASE_PROVIDE_VALID_CUSTOMER_FIRST_NAME, true) -> {
                context.getString(R.string.please_provide_valid_customer_first_name)
            }
            msg.equals(PLEASE_PROVIDE_VALID_CUSTOMER_LAST_NAME, true) -> {
                context.getString(R.string.please_provide_valid_customer_last_name)
            }
            msg.equals(PLEASE_PROVIDE_VALID_COUNTRY_CODE, true) -> {
                context.getString(R.string.please_provide_valid_country_code)
            }
            msg.equals(PLEASE_PROVIDE_VALID_PHONE_NUMBER, true) -> {
                context.getString(R.string.please_provide_valid_phone_number)
            }
            msg.equals(PLEASE_PROVIDE_VALID_EMAIL_ID, true) -> {
                context.getString(R.string.please_provide_valid_email_id)
            }
            msg.equals(EMAIL_ID_ALREADY_EXISTS, true) -> {
                context.getString(R.string.email_id_already_exists)
            }
            msg.equals(PHONE_NUMBER_ALREADY_EXISTS, true) -> {
                context.getString(R.string.phone_number_already_exists)
            }
            else -> {
                context.getString(R.string.something_went_wrong)
            }
        }
    }


    private const val PLEASE_PROVIDE_VALID_EMAIL =  "Please provide valid Email."

    fun getRecoveryLinkErrorMessage(context: Context, msg: String?): String {
        return when {
            msg.equals(PLEASE_PROVIDE_VALID_EMAIL, true) -> {
                context.getString(R.string.please_provide_valid_email)
            }
            msg.equals(YOU_ARE_NOT_A_REGISTER_USER, true) -> {
                context.getString(R.string.you_are_not_a_registerd_user)
            }

            else -> {
                context.getString(R.string.something_went_wrong)
            }
        }
    }

    private const val PLEASE_PROVIDE_NEW_PASSWORD = "Please Provide new Password"
    private const val PLEASE_PROVIDE_CONFIRM_PASSWORD = "Please Provide Confirm Password"
    private const val PASSWORD_AND_CONFIRM_PASSWORD_DOES_NOT_MATCH = "Password and Confirm Password Doesn't Match"

    fun getResetPasswordErrorMessage(context: Context, msg: String?): String {
        return when {
            msg.equals(PLEASE_PROVIDE_NEW_PASSWORD, true) -> {
                context.getString(R.string.please_provide_new_password)
            }
            msg.equals(PLEASE_PROVIDE_CONFIRM_PASSWORD, true) -> {
                context.getString(R.string.please_provide_confirm_password)
            }
            msg.equals(PASSWORD_AND_CONFIRM_PASSWORD_DOES_NOT_MATCH, true) -> {
                context.getString(R.string.password_and_confirm_password_does_not_match)
            }
            msg.equals(PLEASE_PROVIDE_VALID_EMAIL, true) -> {
                context.getString(R.string.please_provide_valid_email)
            }

            else -> {
                context.getString(R.string.something_went_wrong)
            }
        }
    }

    private const val PLEASE_PROVIDE_VALID_OTP= "Please provide a valid otp."
    private const val OTP_DID_NOT_MATCH = "OTP did not match."
    private const val WRONG_EMAIL_OR_OTP_DID_NOT_MATCH = "Wrong email or OTP did not match."

    fun getVerifyOtpErrorMessage(context: Context, msg: String?): String {
        return when {
            msg.equals(PLEASE_PROVIDE_VALID_EMAIL, true) -> {
                context.getString(R.string.please_provide_valid_email)
            }
            msg.equals(PLEASE_PROVIDE_VALID_OTP, true) -> {
                context.getString(R.string.please_provide_a_valid_otp)
            }
            msg.equals(OTP_DID_NOT_MATCH, true) -> {
                context.getString(R.string.otp_did_not_match)
            }
            msg.equals(WRONG_EMAIL_OR_OTP_DID_NOT_MATCH, true) -> {
                context.getString(R.string.otp_did_not_match)
            }

            else -> {
                context.getString(R.string.something_went_wrong)
            }
        }
    }

    fun getResendOtpErrorMessage(context: Context, msg: String?): String {
        return when {
            msg.equals(PLEASE_PROVIDE_VALID_EMAIL, true) -> {
                context.getString(R.string.please_provide_valid_email)
            }
            msg.equals(YOU_ARE_NOT_A_REGISTER_USER, true) -> {
                context.getString(R.string.you_are_not_a_registerd_user)
            }
            else -> {
                context.getString(R.string.something_went_wrong)
            }
        }
    }

    private const val PLEASE_PROVIDE_ADDRESS1 = "Please Provide address1"
    private const val PLEASE_PROVIDE_VALID_IMAGE_TYPE = "Please provide valid image type (jpg, png)."
    private const val PLEASE_ONLY_UPLOAD_FILES_LESS_THAN_5MB = "Please only upload files less than 5mb"
    private const val IMAGE_UPLOAD_FAILED = "Image upload failed."
    private const val CUSTOMER_PROFILE_UPDATED_SUCCESSFULLY = "Customer profile updated successfully."
    private const val FAILED_TO_UPDATE_CUSTOMER_PLEASE_TRY_AGAIN = "Failed to update customer. Please Try Again."

    fun getUpdateProfileErrorMessage(context: Context, msg: String?): String {
        return when {
            msg.equals(PLEASE_PROVIDE_VALID_CUSTOMER_FIRST_NAME, true) -> {
                context.getString(R.string.please_provide_valid_customer_first_name)
            }
            msg.equals(PLEASE_PROVIDE_VALID_CUSTOMER_LAST_NAME, true) -> {
                context.getString(R.string.please_provide_valid_customer_last_name)
            }
            msg.equals(PLEASE_PROVIDE_VALID_COUNTRY_CODE, true) -> {
                context.getString(R.string.please_provide_valid_country_code)
            }
            msg.equals(PLEASE_PROVIDE_VALID_PHONE_NUMBER, true) -> {
                context.getString(R.string.please_provide_valid_phone_number)
            }
            msg.equals(PLEASE_PROVIDE_VALID_EMAIL_ID, true) -> {
                context.getString(R.string.please_provide_valid_email_id)
            }
            msg.equals(EMAIL_ID_ALREADY_EXISTS, true) -> {
                context.getString(R.string.email_id_already_exists)
            }
            msg.equals(PHONE_NUMBER_ALREADY_EXISTS, true) -> {
                context.getString(R.string.phone_number_already_exists)
            }

            msg.equals(PLEASE_PROVIDE_ADDRESS1, true) -> {
                context.getString(R.string.please_provide_address1)
            }
            msg.equals(PLEASE_PROVIDE_VALID_IMAGE_TYPE, true) -> {
                context.getString(R.string.please_provide_valid_image_type)
            }
            msg.equals(PLEASE_ONLY_UPLOAD_FILES_LESS_THAN_5MB, true) -> {
                context.getString(R.string.please_only_upload_files_less_than_5mb)
            }
            msg.equals(CUSTOMER_PROFILE_UPDATED_SUCCESSFULLY, true) -> {
                context.getString(R.string.customer_profile_updated_successfully)
            }
            msg.equals(FAILED_TO_UPDATE_CUSTOMER_PLEASE_TRY_AGAIN, true) -> {
                context.getString(R.string.failed_to_update_customer_please_try_again)
            }
            msg.equals(IMAGE_UPLOAD_FAILED, true) -> {
                context.getString(R.string.image_upload_failed)
            }
            else -> {
                context.getString(R.string.something_went_wrong)
            }
        }
    }

    private const val PLEASE_PROVIDE_VALID_OLD_PASSWORD = "Please provide valid old password."
    private const val PLEASE_PROVIDE_VALID_NEW_PASSWORD = "Please provide valid new password."
    private const val YOUR_OLD_PASSWORD_IS_INCORRECT = "Your old password is incorrect"
    private const val USER_DOES_NOT_EXIST = "User does not exist"


    fun getUpdatePasswordErrorMessage(context: Context, msg: String?): String {
        return when {
            msg.equals(PLEASE_PROVIDE_VALID_OLD_PASSWORD, true) -> {
                context.getString(R.string.please_provide_valid_old_password)
            }
            msg.equals(PLEASE_PROVIDE_VALID_NEW_PASSWORD, true) -> {
                context.getString(R.string.please_provide_valid_new_password)
            }
            msg.equals(YOUR_OLD_PASSWORD_IS_INCORRECT, true) -> {
                context.getString(R.string.your_old_password_is_incorrect)
            }
            msg.equals(USER_DOES_NOT_EXIST, true) -> {
                context.getString(R.string.user_does_not_exist)
            }
            else -> {
                context.getString(R.string.something_went_wrong)
            }
        }
    }

    private const val COUPON_INVALID_OR_EXPIRED = "Coupon invalid or expired."


    fun getMatchCouponErrorMessage(context: Context, msg: String?): String {
        return when {
            msg.equals(COUPON_INVALID_OR_EXPIRED, true) -> {
                context.getString(R.string.coupon_invalid_or_expired)
            }
            else -> {
                context.getString(R.string.something_went_wrong)
            }
        }
    }
    private const val SERVICE_PRICE_UPDATED = "Service price updated."
    private const val PRODUCT_PRICE_UPDATED = "Product price updated."
    private const val FOOD_PRICE_UPDATED = "Food price updated."

    fun getCartPriceChangedMessage(context: Context, msg:String?):String{
        return when {
            msg.equals(SERVICE_PRICE_UPDATED, true) -> {
                context.getString(R.string.service_price_changed)
            }
            msg.equals(PRODUCT_PRICE_UPDATED, true) -> {
                context.getString(R.string.product_price_changed)
            }
            msg.equals(FOOD_PRICE_UPDATED, true) -> {
                context.getString(R.string.food_price_changed)
            }
            else -> {
                context.getString(R.string.price_changed)
            }
        }
    }

    private const val SERVICE_STATUS_UPDATED = "Service status updated"
    private const val PRODUCT_STATUS_UPDATED = "Product status updated"
    private const val FOOD_STATUS_UPDATED = "Food status updated"

    fun getCartStatusChangedMessage(context: Context,msg:String?):String{
        return when {
            msg.equals(SERVICE_STATUS_UPDATED, true) -> {
                context.getString(R.string.service_is_unavailable)
            }
            msg.equals(PRODUCT_STATUS_UPDATED, true) -> {
                context.getString(R.string.product_is_unavailable)
            }
            msg.equals(FOOD_STATUS_UPDATED, true) -> {
                context.getString(R.string.food_is_unavailable)
            }
            else -> {
                context.getString(R.string.services_or_products_or_foods_are_unavailable)
            }
        }
    }

    private const val SERVICE_DELETED = "service deleted"
    private const val PRODUCT_DELETED = "Product deleted"
    private const val FOOD_DELETED = "Food deleted"

    fun getCartItemDeletedMessage(context: Context,msg:String?):String{
        return when {
            msg.equals(SERVICE_DELETED, true) -> {
                context.getString(R.string.service_is_unavailable)
            }
            msg.equals(PRODUCT_DELETED, true) -> {
                context.getString(R.string.product_is_unavailable)
            }
            msg.equals(FOOD_DELETED, true) -> {
                context.getString(R.string.food_is_unavailable)
            }
            else -> {
                context.getString(R.string.services_or_products_or_foods_are_unavailable)
            }
        }
    }

}