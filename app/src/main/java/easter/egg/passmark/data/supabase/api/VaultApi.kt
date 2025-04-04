package easter.egg.passmark.data.supabase.api

import easter.egg.passmark.data.models.content.Vault
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class VaultApi @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    private val table = supabaseClient.from(table = "vaults")

    suspend fun getVaultList(): List<Vault> {
        return table.select().decodeList<Vault>()
    }
}