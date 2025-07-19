package easter.egg.passmark.data.supabase.api

import easter.egg.passmark.data.models.Vault
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class VaultApi @Inject constructor(supabaseClient: SupabaseClient) {
    private val table = supabaseClient.from(table = "vaults")

    suspend fun getVaultList(): List<Vault> = table.select().decodeList<Vault>()

    suspend fun upsert(vault: Vault): Vault = table
        .upsert(value = vault, request = { select() })
        .decodeSingle<Vault>()

    suspend fun delete(vault: Vault): Vault = table
        .delete(
            request = {
                select()
                filter { Vault::id eq vault.id!! }
            }
        )
        .decodeSingle<Vault>()
}