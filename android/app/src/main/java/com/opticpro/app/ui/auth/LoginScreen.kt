package com.opticpro.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.opticpro.app.ui.common.FormField

@Composable
fun LoginScreen(
    justRegistered: Boolean,
    onRegisterClick: () -> Unit,
    vm: LoginViewModel = hiltViewModel(),
) {
    val s by vm.state.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding().padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("OpticPro", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Text("Masuk untuk melanjutkan", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp))

        if (justRegistered) {
            Text("Registrasi berhasil. Silakan masuk.", color = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.height(12.dp))
        }
        FormField(s.email, vm::onEmail, "Email", s.fieldErrors["email"], enabled = !s.loading, keyboardType = KeyboardType.Email)
        FormField(s.password, vm::onPassword, "Password", s.fieldErrors["password"], enabled = !s.loading, password = true)
        s.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = vm::submit, enabled = !s.loading, modifier = Modifier.fillMaxWidth()) {
            if (s.loading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp) else Text("Masuk")
        }
        TextButton(onClick = onRegisterClick, enabled = !s.loading, modifier = Modifier.fillMaxWidth()) {
            Text("Belum punya akun? Daftar")
        }
    }
}