package com.opticpro.app.data.repository

import com.opticpro.app.data.local.Session
import com.opticpro.app.data.local.TokenStore
import com.opticpro.app.data.remote.ApiResponse
import com.opticpro.app.data.remote.AuthApi
import com.opticpro.app.data.remote.ErrorData
import com.opticpro.app.data.remote.LoginRequest
import com.opticpro.app.data.remote.RefreshRequest
import com.opticpro.app.data.remote.RegisterRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Failure(val message: String, val fieldErrors: Map<String, String> = emptyMap()) : ApiResult<Nothing>
}

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val store: TokenStore,
    private val json: Json,
) {
    val session: Flow<Session?> = store.session

    suspend fun login(email: String, password: String): ApiResult<Unit> = safeCall {
        val data = api.login(LoginRequest(email, password)).data ?: error("Respons kosong")
        store.save(data.accessToken, data.refreshToken, data.user)
    }

    suspend fun register(
        nama: String, email: String, telp: String, password: String, konfirmasi: String,
    ): ApiResult<Unit> = safeCall {
        api.register(RegisterRequest(nama, email, telp, password, konfirmasi))
        Unit
    }

    // Cabut refresh token di server (best effort), lalu hapus token lokal
    suspend fun logout() {
        val current = store.current()
        if (current != null) {
            try {
                api.logout(RefreshRequest(current.refreshToken))
            } catch (_: Exception) {
                // tetap logout lokal
            }
        }
        store.clear()
    }

    private suspend fun <T> safeCall(block: suspend () -> T): ApiResult<T> = try {
        ApiResult.Success(block())
    } catch (e: HttpException) {
        val body = try {
            e.response()?.errorBody()?.string()?.let { json.decodeFromString<ApiResponse<ErrorData>>(it) }
        } catch (_: Exception) {
            null
        }
        ApiResult.Failure(
            body?.message?.ifBlank { null } ?: "Terjadi kesalahan (${e.code()})",
            body?.data?.errors.orEmpty(),
        )
    } catch (e: IOException) {
        ApiResult.Failure("Tidak dapat terhubung ke server. Periksa koneksi internet Anda.")
    } catch (e: Exception) {
        ApiResult.Failure("Terjadi kesalahan tak terduga.")
    }
}