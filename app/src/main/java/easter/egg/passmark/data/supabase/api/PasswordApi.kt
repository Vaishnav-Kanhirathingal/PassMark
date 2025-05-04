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

    suspend fun updateUsageStat(
        lastUsed: Long,
        usedCount: Int,
        cloudId: Int
    ): PasswordCapsule = table
        .update(
            update = {
                set(column = PasswordCapsule.Companion.Keys.LAST_USED_KEY, value = lastUsed)
                set(column = PasswordCapsule.Companion.Keys.USED_COUNT_KEY, value = usedCount)
            },
            request = {
                select()
                filter {
                    eq(
                        column = PasswordCapsule.Companion.Keys.SUPABASE_ID_KEY,
                        value = cloudId
                    )
                }
            }
        )
        .decodeSingle<PasswordCapsule>()
}