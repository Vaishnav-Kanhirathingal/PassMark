package easter.egg.passmark.data.supabase.api

import easter.egg.passmark.data.models.content.Vault
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

class VaultApi @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    fun getVaultList():List<Vault> {
        TODO()
    }
}