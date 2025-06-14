package easter.egg.passmark.utils.testing

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import easter.egg.passmark.data.models.password.PasswordSortingOptions

object TestTags {
    // TODO: remove this after testing
    fun Modifier.applyTag(testTag: String): Modifier = this
        .testTag(tag = testTag)
        .semantics { this.contentDescription = testTag }

    enum class Login {
        GOOGLE_BUTTON
    }

    enum class CreateMasterKey {
        TEXT_FIELD,
        VISIBILITY_BUTTON,
        CONFIRM_BUTTON,
    }

    enum class Home {
        CREATE_NEW_PASSWORD_BUTTON;

        enum class TopBar {
            SORTING_BUTTON,
            SEARCH_BUTTON,
            SEARCH_TEXT_FIELD,
            BACK_BUTTON,
            OPEN_DRAWER_BUTTON;
        }

        enum class Sorting {
            INCREASING_ORDER,
            DECREASING_ORDER;

            companion object {
                fun getSortOptionTag(passwordSortingOptions: PasswordSortingOptions): String {
                    return passwordSortingOptions.name
                }
            }
        }

        companion object {
            fun getPasswordTag(name: String): String = "$name password"
            fun getPasswordOptionsTag(name: String): String = "$name options"
        }

        enum class PasswordOptionsBottomSheet {
            EDIT_BUTTON
        }

        enum class Drawer {
            TOP_TITLE,
            CREATE_NEW_VAULT_BUTTON,
            SETTINGS;

            enum class VaultDialog {
                TEXT_FIELD,
                CONFIRM_BUTTON,
                DELETE_BUTTON;

                companion object {
                    fun getIconTag(index: Int) = "icon_$index"
                }
            }
        }
    }

    enum class EditPassword {
        DISMISS,
        SELECT_VAULT_BUTTON,
        SELECT_VAULT_DIALOG_CHOOSE_VAULT,
        SAVE_BUTTON,
        TITLE_TEXT_FIELD,
        EMAIL_TEXT_FIELD,
        USER_NAME_TEXT_FIELD,
        PASSWORD_TEXT_FIELD,
        WEBSITE_TEXT_FIELD,
        NOTES_TEXT_FIELD,
        USE_FINGERPRINT_SWITCH,
        KEEP_LOCAL_SWITCH;

        object ChooseVault {
            fun getVaultTestTag(vaultName: String) = "Vault $vaultName"
        }
    }

    enum class ViewPassword {
        PASSWORD_HISTORY_BUTTON,
        FINGERPRINT_BUTTON,
        DELETE_BUTTON,
    }

    enum class ConfirmationDialog { POSITIVE_BUTTON }

    enum class Settings {
        LOCAL_STORAGE_SWITCH,
        FINGERPRINT_AUTHENTICATION_SWITCH,
        RESET_ACCOUNT_BUTTON,
        CHANGE_PASSWORD_BUTTON,
        LOG_OUT
    }

    enum class ChangePassword {
        ORIGINAL_PASSWORD_TEXT_FIELD,
        NEW_PASSWORD_TEXT_FIELD,
        NEW_PASSWORD_REPEATED_TEXT_FIELD,
        CONFIRM_BUTTON
    }

    enum class ErrorScreen {
        RETRY_BUTTON
    }

    enum class AutoLock {
        PASSWORD_TEXT_FIELD, VISIBILITY_BUTTON, FINGERPRINT_BUTTON, CONFIRM_BUTTON
    }
}