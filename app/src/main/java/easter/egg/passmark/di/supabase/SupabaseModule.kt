package easter.egg.passmark.di.supabase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import easter.egg.passmark.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SupabaseModule {
    companion object {
        val mockClient = createSupabaseClient(
            supabaseKey = "",
            supabaseUrl = "",
            builder = { install(Postgrest) }
        )
    }

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY,
        builder = {
            install(Auth)
            install(Postgrest)
        }
    )
}