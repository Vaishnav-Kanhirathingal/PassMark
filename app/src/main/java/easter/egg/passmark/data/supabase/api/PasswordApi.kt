package easter.egg.passmark.data.supabase.api

import easter.egg.passmark.data.models.content.Password
import easter.egg.passmark.data.models.content.PasswordCapsule
import easter.egg.passmark.utils.security.PasswordCryptographyHandler
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class PasswordApi @Inject constructor(
    supabaseClient: SupabaseClient
) {
    private val table = supabaseClient.from("passwords")

    suspend fun savePassword(passwordCapsule: PasswordCapsule): PasswordCapsule {
        return table
            .upsert(value = passwordCapsule, request = { select() })
            .decodeSingle<PasswordCapsule>()
    }

    suspend fun getPasswordList(passwordCryptographyHandler: PasswordCryptographyHandler): List<Password> {
        return table.select().decodeList<PasswordCapsule>().map {
            it.toPassword(passwordCryptographyHandler = passwordCryptographyHandler)
        }
    }

    suspend fun deletePassword(passwordId: Int) {
        table.delete {
            filter {
                eq(
                    column = PasswordCapsule.Companion.Keys.SUPABASE_ID_KEY,
                    value = passwordId
                )
            }
        }
    }
}