package easter.egg.passmark.data.api

import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

class VaultApi @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    fun getVaultList(){
        TODO()
    }
}