package com.opticpro.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opticpro.app.data.repository.ApiResult
import com.opticpro.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
)

@HiltViewModel
class LoginViewModel @Inject constructor(private val repo: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmail(v: String) = _state.update { it.copy(email = v, error = null, fieldErrors = it.fieldErrors - "email") }
    fun onPassword(v: String) = _state.update { it.copy(password = v, error = null, fieldErrors = it.fieldErrors - "password") }

    fun submit() {
        val s = _state.value
        if (s.loading) return
        val errors = AuthValidators.validateLogin(s.email, s.password)
        if (errors.isNotEmpty()) {
            _state.update { it.copy(fieldErrors = errors) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            when (val r = repo.login(s.email.trim(), s.password)) {
                // Sukses: sesi tersimpan, AppNavHost otomatis pindah ke beranda sesuai peran
                is ApiResult.Success -> _state.update { it.copy(loading = false) }
                is ApiResult.Failure -> _state.update { it.copy(loading = false, error = r.message, fieldErrors = r.fieldErrors) }
            }
        }
    }
}

data class RegisterUiState(
    val nama: String = "",
    val email: String = "",
    val telepon: String = "",
    val password: String = "",
    val konfirmasi: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
    val registered: Boolean = false,
)

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repo: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    private fun edit(field: String, block: (RegisterUiState) -> RegisterUiState) =
        _state.update { block(it).copy(error = null, fieldErrors = it.fieldErrors - field) }

    fun onNama(v: String) = edit("nama_lengkap") { it.copy(nama = v) }
    fun onEmail(v: String) = edit("email") { it.copy(email = v) }
    fun onTelepon(v: String) = edit("no_telepon") { it.copy(telepon = v) }
    fun onPassword(v: String) = edit("password") { it.copy(password = v) }
    fun onKonfirmasi(v: String) = edit("konfirmasi_password") { it.copy(konfirmasi = v) }

    fun submit() {
        val s = _state.value
        if (s.loading) return
        val errors = AuthValidators.validateRegister(s.nama, s.email, s.telepon, s.password, s.konfirmasi)
        if (errors.isNotEmpty()) {
            _state.update { it.copy(fieldErrors = errors) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            when (val r = repo.register(s.nama.trim(), s.email.trim(), s.telepon.trim(), s.password, s.konfirmasi)) {
                is ApiResult.Success -> _state.update { it.copy(loading = false, registered = true) }
                is ApiResult.Failure -> _state.update { it.copy(loading = false, error = r.message, fieldErrors = r.fieldErrors) }
            }
        }
    }
}