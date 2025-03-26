package easter.egg.passmark.data.supabase.account

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import javax.inject.Inject

class SupabaseAccountHelper @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    suspend fun getId(): String {
        return supabaseClient.auth.retrieveUserForCurrentSession().id
    }
}