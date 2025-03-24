package easter.egg.passmark.data.api

import easter.egg.passmark.data.models.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class UserApi @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    companion object {
        const val TABLE_NAME = "users"
    }

    suspend fun getUser(): User? = supabaseClient.from(TABLE_NAME)
        .select(request = {})
        .decodeSingleOrNull<User>()
}