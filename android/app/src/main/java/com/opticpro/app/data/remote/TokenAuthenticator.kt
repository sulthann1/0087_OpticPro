package com.opticpro.app.data.remote

import com.opticpro.app.data.local.TokenStore
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val store: TokenStore,
    private val api: Lazy<AuthApi>, // Lazy menghindari siklus ketergantungan OkHttp -> Authenticator -> Api -> OkHttp
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header("No-Auth") != null) return null
        if (priorResponses(response) >= 2) return null // maksimal satu kali percobaan ulang

        synchronized(this) {
            val session = store.current() ?: return null
            val sentToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            // Thread lain sudah memperbarui token: cukup ulang dengan token terbaru
            if (sentToken != null && sentToken != session.accessToken) {
                return response.request.withToken(session.accessToken)
            }

            return try {
                val data = runBlocking { api.get().refresh(RefreshRequest(session.refreshToken)) }.data ?: return null
                runBlocking { store.save(data.accessToken, data.refreshToken, data.user) }
                response.request.withToken(data.accessToken)
            } catch (e: HttpException) {
                runBlocking { store.clear() } // refresh ditolak server: sesi berakhir
                null
            } catch (e: Exception) {
                null // masalah jaringan: sesi dipertahankan
            }
        }
    }

    private fun Request.withToken(token: String): Request =
        newBuilder().header("Authorization", "Bearer $token").build()

    private fun priorResponses(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}