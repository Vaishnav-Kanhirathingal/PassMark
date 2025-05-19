package easter.egg.passmark.utils.testing

object TestTags {
    // TODO: remove this
    const val timeout = 1000L

    enum class Login {
        GOOGLE_BUTTON,
    }

    enum class CreateMasterKey {
        TEXT_FIELD,
        CONFIRM_BUTTON,
    }

    enum class Home {
        CREATE_NEW_PASSWORD_BUTTON,
        OPEN_DRAWER_BUTTON;

        enum class Drawer {
            TOP_TITLE,
            CREATE_NEW_VAULT_BUTTON;

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
//        KEEP_LOCAL_SWITCH // TODO: check use case
    }
}