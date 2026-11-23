package com.opticpro.app.data.local

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.opticpro.app.data.remote.UserDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore by preferencesDataStore(name = "opticpro_session")

data class Session(
    val accessToken: String,
    val refreshToken: String,
    val userId: Int,
    val nama: String,
    val email: String,
    val role: String,
)

@Singleton
class TokenStore @Inject constructor(@ApplicationContext private val context: Context) {

    private object Keys {
        val ACCESS = stringPreferencesKey("access")
        val REFRESH = stringPreferencesKey("refresh")
        val USER_ID = intPreferencesKey("user_id")
        val NAMA = stringPreferencesKey("nama")
        val EMAIL = stringPreferencesKey("email")
        val ROLE = stringPreferencesKey("role")
    }

    private val aead: Aead by lazy {
        AeadConfig.register()
        AndroidKeysetManager.Builder()
            .withSharedPref(context, "opticpro_keyset", "opticpro_keyset_prefs")
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri("android-keystore://opticpro_master_key")
            .build()
            .keysetHandle
            .getPrimitive(Aead::class.java)
    }

    private fun encrypt(plain: String): String =
        Base64.encodeToString(aead.encrypt(plain.toByteArray(Charsets.UTF_8), ByteArray(0)), Base64.NO_WRAP)

    private fun decrypt(cipher: String): String =
        String(aead.decrypt(Base64.decode(cipher, Base64.NO_WRAP), ByteArray(0)), Charsets.UTF_8)

    private fun Preferences.toSession(): Session? {
        val access = this[Keys.ACCESS]
        val refresh = this[Keys.REFRESH]
        val role = this[Keys.ROLE]
        if (access == null || refresh == null || role == null) return null
        return try {
            Session(
                accessToken = decrypt(access),
                refreshToken = decrypt(refresh),
                userId = this[Keys.USER_ID] ?: 0,
                nama = this[Keys.NAMA].orEmpty(),
                email = this[Keys.EMAIL].orEmpty(),
                role = role,
            )
        } catch (e: Exception) {
            null // data rusak atau kunci berubah: dianggap belum login
        }
    }

    val session: Flow<Session?> = context.sessionDataStore.data.map { it.toSession() }

    suspend fun save(accessToken: String, refreshToken: String, user: UserDto) {
        context.sessionDataStore.edit {
            it[Keys.ACCESS] = encrypt(accessToken)
            it[Keys.REFRESH] = encrypt(refreshToken)
            it[Keys.USER_ID] = user.id
            it[Keys.NAMA] = user.namaLengkap
            it[Keys.EMAIL] = user.email
            it[Keys.ROLE] = user.role
        }
    }

    suspend fun clear() {
        context.sessionDataStore.edit { it.clear() }
    }

    // Dipakai OkHttp (thread I/O, bukan main thread)
    fun current(): Session? = runBlocking { session.first() }
}