package easter.egg.passmark.data.supabase.api

import easter.egg.passmark.data.models.content.Password
import easter.egg.passmark.utils.security.PasswordCryptographyHandler
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class PasswordApi @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    private val table = supabaseClient.from("passwords")

    suspend fun savePassword(
        password: Password,
        passwordCryptographyHandler: PasswordCryptographyHandler
    ) = table.upsert(
        value = password, // TODO: save password capsule
        request = { select() }
    )

    suspend fun getPasswordList(
        passwordCryptographyHandler: PasswordCryptographyHandler
    ): List<Password> {
        TODO()
    }
}