package org.thoughtcrime.securesms.util

import io.github.jan.tennert.supabase.SupabaseClient
import io.github.jan.tennert.supabase.createSupabaseClient
import io.github.jan.tennert.supabase.auth.Auth
import io.github.jan.tennert.supabase.postgrest.Postgrest
import io.github.jan.tennert.supabase.realtime.Realtime
import io.github.jan.tennert.supabase.storage.Storage

object SupabaseHelper {
    private const val SUPABASE_URL = "https://rpnnvizramehvlfudnnr.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJwbm52aXpyYW1laHZsZnVkbm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzc1NzE2ODgsImV4cCI6MjA5MzE0NzY4OH0.M8hHS86KAweQWjXyLm3mTjEU4H-FkCp55r7HQwJeugg"

    lateinit var client: SupabaseClient
        private set

    fun init() {
        client = createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
            install(Realtime)
        }
    }
}
