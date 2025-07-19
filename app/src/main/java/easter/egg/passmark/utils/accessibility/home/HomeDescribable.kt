package easter.egg.passmark.utils.accessibility.home

import easter.egg.passmark.utils.accessibility.Describable

enum class HomeDescribable(override val desc: String) : Describable {
    CREATE_NEW_PASSWORD(desc = "Create a new password");

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
        }
    }

    companion object {
        fun getPasswordTag(name: String): Describable {
            return object : Describable {
                override val desc: String get() = "$name password"
            }
        }

        fun getPasswordOptionsTag(name: String): Describable {
            return object : Describable {
                override val desc: String get() = "$name options"
            }
        }
    }

    enum class PasswordOptionsBottomSheet(override val desc: String) : Describable {
        EDIT_BUTTON(desc = "Edit password"), // TODO: add others
    }

    enum class Drawer(override val desc: String) : Describable {
        CREATE_NEW_VAULT_BUTTON(desc = "Create vault"),
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
            UPDATE_BUTTON(desc = "Update vault");

            companion object {
                fun deleteVaultDescribable(vaultName: String): Describable {
                    return object : Describable {
                        override val desc: String get() = "Delete Vault $vaultName"
                    }
                }

//                    fun getIconDescription(index: Int) = "icon_$index"
            }
        }
    }
}
