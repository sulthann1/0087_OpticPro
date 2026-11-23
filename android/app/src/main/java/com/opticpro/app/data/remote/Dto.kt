package com.opticpro.app.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Amplop respons API: {success, message, data}
@Serializable
data class ApiResponse<T>(val success: Boolean, val message: String = "", val data: T? = null)

@Serializable
data class ErrorData(val errors: Map<String, String> = emptyMap())

@Serializable
data class UserDto(
    val id: Int,
    @SerialName("nama_lengkap") val namaLengkap: String,
    val email: String,
    @SerialName("no_telepon") val noTelepon: String,
    val role: String,
)

@Serializable
data class AuthData(val accessToken: String, val refreshToken: String, val user: UserDto)

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegisterRequest(
    @SerialName("nama_lengkap") val namaLengkap: String,
    val email: String,
    @SerialName("no_telepon") val noTelepon: String,
    val password: String,
    @SerialName("konfirmasi_password") val konfirmasiPassword: String,
)

@Serializable
data class RefreshRequest(val refreshToken: String)