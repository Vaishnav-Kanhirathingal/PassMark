package easter.egg.passmark.di.supabase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import easter.egg.passmark.data.supabase.account.SupabaseAccountHelper
import easter.egg.passmark.data.supabase.api.PasswordApi
import easter.egg.passmark.data.supabase.api.UserApi
import io.github.jan.supabase.SupabaseClient

@Module
@InstallIn(ViewModelComponent::class)
class SupabaseApiModule {

    @Provides
    @ViewModelScoped
    fun providePasswordApi(supabaseClient: SupabaseClient): PasswordApi =
        PasswordApi(supabaseClient = supabaseClient)

    @Provides
    @ViewModelScoped
    fun provideUserApi(supabaseClient: SupabaseClient): UserApi =
        UserApi(supabaseClient = supabaseClient)

    @Provides
    @ViewModelScoped
    fun providesSupabaseAccountHelper(supabaseClient: SupabaseClient): SupabaseAccountHelper =
        SupabaseAccountHelper(supabaseClient = supabaseClient)
}