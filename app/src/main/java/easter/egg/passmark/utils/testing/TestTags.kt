package easter.egg.passmark.utils.testing

object TestTags {
    enum class Login {
        GOOGLE_BUTTON,
    }

    enum class CreateMasterKey {
        TEXT_FIELD,
        CONFIRM_BUTTON,
    }

    enum class Home {
            CREATE_NEW_PASSWORD_BUTTON,
            SEARCH_BUTTON,
            SORT_BUTTON;
        enum class Drawer {
            CREATE_NEW_VAULT_BUTTON,
            SETTINGS_BUTTON;

            enum class VaultDialog {
                TEXT_FIELD,
                CONFIRM_BUTTON,
                DELETE_BUTTON
            }
        }
    }

    enum class UpdatePassword {
        // TODO: pending
    }
}