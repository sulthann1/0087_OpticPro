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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.opticpro.app.ui.common.FormField

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onBackToLogin: () -> Unit,
    vm: RegisterViewModel = hiltViewModel(),
) {
    val s by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(s.registered) { if (s.registered) onRegistered() }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding().padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Buat Akun", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        FormField(s.nama, vm::onNama, "Nama lengkap", s.fieldErrors["nama_lengkap"], enabled = !s.loading)
        FormField(s.email, vm::onEmail, "Email", s.fieldErrors["email"], enabled = !s.loading, keyboardType = KeyboardType.Email)
        FormField(s.telepon, vm::onTelepon, "No telepon", s.fieldErrors["no_telepon"], enabled = !s.loading, keyboardType = KeyboardType.Phone)
        FormField(s.password, vm::onPassword, "Password", s.fieldErrors["password"], enabled = !s.loading, password = true)
        FormField(s.konfirmasi, vm::onKonfirmasi, "Konfirmasi password", s.fieldErrors["konfirmasi_password"], enabled = !s.loading, password = true)
        s.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = vm::submit, enabled = !s.loading, modifier = Modifier.fillMaxWidth()) {
            if (s.loading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp) else Text("Daftar")
        }
        TextButton(onClick = onBackToLogin, enabled = !s.loading, modifier = Modifier.fillMaxWidth()) {
            Text("Sudah punya akun? Masuk")
        }
    }
}