package ie.healthylunch.app.ui.base


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ie.healthylunch.app.data.repository.*
import ie.healthylunch.app.data.viewModel.*


@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {

            modelClass.isAssignableFrom(CalenderViewModel::class.java) -> CalenderViewModel(
                repository as CalenderRepository
            ) as T
            modelClass.isAssignableFrom(ParentRegistrationStepTwoViewModel::class.java) -> ParentRegistrationStepTwoViewModel(
                repository as ParentRegistrationRepository
            ) as T
            modelClass.isAssignableFrom(ParentRegistrationStepOneViewModel::class.java) -> ParentRegistrationStepOneViewModel(
                repository as ParentRegistrationRepository
            ) as T
            modelClass.isAssignableFrom(ParentRegistrationOtpViewModel::class.java) -> ParentRegistrationOtpViewModel(
                repository as ParentRegistrationRepository
            ) as T
            modelClass.isAssignableFrom(AddNewStudentStepOneViewModel::class.java) -> AddNewStudentStepOneViewModel(
                repository as StudentRepository
            ) as T

            modelClass.isAssignableFrom(ForgotPasswordStepOneViewModel::class.java) -> ForgotPasswordStepOneViewModel(
                repository as ForgotPasswordRepository
            ) as T
            modelClass.isAssignableFrom(ForgotPasswordStepTwoViewModel::class.java) -> ForgotPasswordStepTwoViewModel(
                repository as ForgotPasswordRepository
            ) as T
            modelClass.isAssignableFrom(ForgotPasswordSuccessViewModel::class.java) -> ForgotPasswordSuccessViewModel(
                repository as ForgotPasswordRepository
            ) as T
            modelClass.isAssignableFrom(ResetPasswordViewModel::class.java) -> ResetPasswordViewModel(
                repository as ForgotPasswordRepository
            ) as T


            modelClass.isAssignableFrom(DashBoardViewModel::class.java) -> DashBoardViewModel(
                repository as DashBoardRepository
            ) as T
            modelClass.isAssignableFrom(ParentProfileViewViewModel::class.java) -> ParentProfileViewViewModel(
                repository as DashBoardRepository
            ) as T

            modelClass.isAssignableFrom(AddNewStudentStepTwoViewModel::class.java) -> AddNewStudentStepTwoViewModel(
                repository as StudentRepository
            ) as T
            modelClass.isAssignableFrom(AllergenViewModel::class.java) -> AllergenViewModel(
                repository as StudentRepository
            ) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(
                repository as LoginRepository
            ) as T
            modelClass.isAssignableFrom(DashBoardViewModel::class.java) -> DashBoardViewModel(
                repository as DashBoardRepository
            ) as T
            /*   modelClass.isAssignableFrom(QuickViewForStudentViewModel::class.java) -> QuickViewForStudentViewModel(
                   repository as QuickViewForStudentModelRepository
               ) as T*/
            modelClass.isAssignableFrom(PrivacyPolicyViewModel::class.java) -> PrivacyPolicyViewModel(
                repository as PrivacyPolicyViewRepository
            ) as T
            modelClass.isAssignableFrom(TermsOfUseViewModel::class.java) -> TermsOfUseViewModel(
                repository as PrivacyPolicyViewRepository
            ) as T
            modelClass.isAssignableFrom(AboutViewModel::class.java) -> AboutViewModel(
                repository as PrivacyPolicyViewRepository
            ) as T
            modelClass.isAssignableFrom(EditParentProfileViewModel::class.java) -> EditParentProfileViewModel(
                repository as EditParentProfileRepository
            ) as T
            modelClass.isAssignableFrom(NotificationViewModel::class.java) -> NotificationViewModel(
                repository as NotificationRepository
            ) as T
            modelClass.isAssignableFrom(ChangePasswordActivityViewModel::class.java) -> ChangePasswordActivityViewModel(
                repository as ChangePasswordActivityRepository
            ) as T
            modelClass.isAssignableFrom(ViewAddedStudentProfileListViewModel::class.java) -> ViewAddedStudentProfileListViewModel(
                repository as StudentRepository
            ) as T
            modelClass.isAssignableFrom(EditStudentAllergenViewModel::class.java) -> EditStudentAllergenViewModel(
                repository as StudentRepository
            ) as T
            modelClass.isAssignableFrom(EditStudentProfileViewModel::class.java) -> EditStudentProfileViewModel(
                repository as StudentRepository
            ) as T
            modelClass.isAssignableFrom(FeedBackViewModel::class.java) -> FeedBackViewModel(
                repository as FeedBackRepository
            ) as T
            modelClass.isAssignableFrom(NotificationOnOffViewModel::class.java) -> NotificationOnOffViewModel(
                repository as NotificationRepository
            ) as T
            modelClass.isAssignableFrom(ParentViewWalletPageOneActivityViewModel::class.java) -> ParentViewWalletPageOneActivityViewModel(
                repository as ParentViewWalletPageOneActivityRepository
            ) as T

            modelClass.isAssignableFrom(FeedBackViewModel::class.java) -> FeedBackViewModel(
                repository as FeedBackRepository
            ) as T
            modelClass.isAssignableFrom(MenuTemplateViewModel::class.java) -> MenuTemplateViewModel(
                repository as MenuTemplateRepository
            ) as T
            modelClass.isAssignableFrom(ProductViewModel::class.java) -> ProductViewModel(
                repository as ProductRepository
            ) as T
            modelClass.isAssignableFrom(QuickViewForStudentViewModel::class.java) -> QuickViewForStudentViewModel(
                repository as QuickViewForStudentRepository
            ) as T
            modelClass.isAssignableFrom(AppControllerRefreshTokenViewModel::class.java) -> AppControllerRefreshTokenViewModel(
                repository as RefreshTokenRepository
            ) as T
            modelClass.isAssignableFrom(ParentTopUpNowViewModel::class.java) -> ParentTopUpNowViewModel(
                repository as ParentTopUpRepository
            ) as T
            modelClass.isAssignableFrom(AddNewCardViewModel::class.java) -> AddNewCardViewModel(
                repository as AddNewCardRepository
            ) as T
            modelClass.isAssignableFrom(TransactionListViewModel::class.java) -> TransactionListViewModel(
                repository as TransactionListRepository
            ) as T
            modelClass.isAssignableFrom(AddVoucherCodeViewModel::class.java) -> AddVoucherCodeViewModel(
                repository as AddVoucherCodeRepository
            ) as T
            modelClass.isAssignableFrom(RemoveProductsHavingMayAllergenViewModel::class.java) -> RemoveProductsHavingMayAllergenViewModel(
                repository as RemoveProductsHavingMayAllergenRepository
            ) as T
            modelClass.isAssignableFrom(LoginRegistrationViewModel::class.java) -> LoginRegistrationViewModel(
                repository as LoginRegistrationRepository
            ) as T
            modelClass.isAssignableFrom(DeisStudentUniqueCodeViewModel::class.java) -> DeisStudentUniqueCodeViewModel(
                repository as DeisStudentUniqueCodeRepository
            ) as T
            modelClass.isAssignableFrom(DeisStudentSchoolDetailsViewModel::class.java) -> DeisStudentSchoolDetailsViewModel(
                repository as DeisStudentSchoolDetailsRepository
            ) as T
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> FavoritesViewModel(
                repository as FavoritesRepository
            ) as T


            else -> throw IllegalArgumentException("ViewModelClass Not found")
        }
    }
}
