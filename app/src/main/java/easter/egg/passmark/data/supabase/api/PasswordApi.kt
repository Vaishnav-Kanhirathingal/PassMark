package easter.egg.passmark.data.supabase.api

import easter.egg.passmark.data.models.Password
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class PasswordApi @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    val table = supabaseClient.from("passwords")

    suspend fun savePassword(
        password: Password
    ) = table.upsert(
        value = password, // TODO: save password capsule
        request = { select() }
    )

}