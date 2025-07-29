package easter.egg.passmark.utils.accessibility.screens

import easter.egg.passmark.data.models.Vault
import easter.egg.passmark.data.models.password.PasswordSortingOptions
import easter.egg.passmark.utils.accessibility.Describable

enum class HomeDescribable(override val desc: String) : Describable {
    PASSWORD_LIST(desc = "password list"),
    CREATE_NEW_PASSWORD(desc = "create a new password");

    companion object {
        fun getPasswordDescribable(name: String): Describable {
            return object : Describable {
                override val desc: String get() = "$name password"
            }
        }

        fun getPasswordOptionsDescribable(name: String): Describable {
            return object : Describable {
                override val desc: String get() = "$name options"
            }
        }
    }

    enum class TopBar(override val desc: String) : Describable {
        SORTING(desc = "Sorting options"),
        SEARCH_BUTTON(desc = "Search"),
        SEARCH_TEXT_FIELD(desc = "Search box"),
        BACK_BUTTON(desc = "Back"),
        OPEN_DRAWER_BUTTON(desc = "Open drawer");

        enum class Sorting(override val desc: String) : Describable {
            INCREASING_ORDER(desc = "Increasing"),
            DECREASING_ORDER(desc = "Decreasing"),
            NAME(desc = "Sort by name"),
            USAGE(desc = "Sort by usage"),
            LAST_USED(desc = "Sort by last used"),
            CREATED(desc = "Sort by time");

            companion object {
                fun getSortOptionDescribable(passwordSortingOptions: PasswordSortingOptions): Sorting {
                    return when (passwordSortingOptions) {
                        PasswordSortingOptions.NAME -> NAME
                        PasswordSortingOptions.CREATED -> CREATED
                        PasswordSortingOptions.USAGE -> USAGE
                        PasswordSortingOptions.LAST_USED -> LAST_USED
                    }
                }
            }
        }
    }

    enum class PasswordOptionsBottomSheet(override val desc: String) : Describable {
        EDIT_BUTTON(desc = "Edit password"), // TODO: add others
    }

    enum class Drawer(override val desc: String) : Describable {
        CREATE_NEW_VAULT_BUTTON(desc = "Create a new vault"),
        SETTINGS(desc = "Settings"),
        DOCUMENTATION(desc = "Documentation"),
        EXIT(desc = "Exit");

        companion object {
            fun getVaultDescribable(vaultName: String): Describable {
                return object : Describable {
                    override val desc: String
                        get() = "Vault $vaultName filter"
                }
            }
        }

        enum class VaultDialog(override val desc: String) : Describable {
            TEXT_FIELD(desc = "Vault name"),
            CREATE_BUTTON(desc = "Create vault"),
            UPDATE_BUTTON(desc = "Update vault"),
            DELETE_VAULT_BUTTON(desc = "Delete Vault");

            companion object {
                fun getIconDescribable(index: Int) = object : Describable {
                    override val desc: String
                        get() = "Vault Icon ${Vault.Companion.iconList.getOrNull(index = index)?.name}"

                }
            }
        }
    }
}