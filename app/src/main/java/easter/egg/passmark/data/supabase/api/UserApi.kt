package easter.egg.passmark.data.supabase.api

import easter.egg.passmark.data.models.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class UserApi @Inject constructor(
    supabaseClient: SupabaseClient
) {
    private val table = supabaseClient.from(table = "users")

    suspend fun getUser(): User? = table.select().decodeSingleOrNull<User>()
    suspend fun setUser(user: User) = table.upsert(value = user)
    suspend fun deleteUser() = table.delete(request = {}) // TODO: test
}