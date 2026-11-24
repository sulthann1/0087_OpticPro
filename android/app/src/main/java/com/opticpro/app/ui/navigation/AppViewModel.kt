package com.opticpro.app.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opticpro.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserSession(val userId: Int, val nama: String, val email: String, val role: String) {
    val isAdmin get() = role == "ADMIN"
}

sealed interface SessionState {
    data object Loading : SessionState
    data object LoggedOut : SessionState
    data class LoggedIn(val user: UserSession) : SessionState
}

@HiltViewModel
class AppViewModel @Inject constructor(private val repo: AuthRepository) : ViewModel() {

    val sessionState: StateFlow<SessionState> = repo.session
        .map { s ->
            if (s == null) SessionState.LoggedOut
            else SessionState.LoggedIn(UserSession(s.userId, s.nama, s.email, s.role))
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SessionState.Loading)

    fun logout() {
        viewModelScope.launch { repo.logout() }
    }
}