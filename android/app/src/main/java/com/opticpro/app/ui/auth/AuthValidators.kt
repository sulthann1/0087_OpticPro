package com.opticpro.app.ui.auth

object AuthValidators {
    private val EMAIL = Regex("""^[^\s@]+@[^\s@]+\.[^\s@]+$""")
    private val TELP = Regex("""^\d{10,15}$""")

    fun validateLogin(email: String, password: String): Map<String, String> = buildMap {
        if (!EMAIL.matches(email.trim())) put("email", "Format email tidak valid")
        if (password.isEmpty()) put("password", "Password wajib diisi")
    }

    fun validateRegister(
        nama: String, email: String, telp: String, password: String, konfirmasi: String,
    ): Map<String, String> = buildMap {
        if (nama.trim().length !in 3..100) put("nama_lengkap", "Nama harus 3 sampai 100 karakter")
        if (!EMAIL.matches(email.trim())) put("email", "Format email tidak valid")
        if (!TELP.matches(telp.trim())) put("no_telepon", "No telepon harus 10 sampai 15 digit")
        if (password.length < 8 || password.none { it.isLetter() } || password.none { it.isDigit() }) {
            put("password", "Password minimal 8 karakter dan berisi huruf serta angka")
        }
        if (konfirmasi != password) put("konfirmasi_password", "Konfirmasi password tidak sama")
    }
}