package com.opticpro.app.ui.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthValidatorsTest {

    @Test fun login_valid_tanpa_kesalahan() {
        assertTrue(AuthValidators.validateLogin("budi@opticpro.test", "Customer123").isEmpty())
    }

    @Test fun login_email_salah_dan_password_kosong() {
        val e = AuthValidators.validateLogin("bukan-email", "")
        assertEquals(setOf("email", "password"), e.keys)
    }

    @Test fun register_valid_tanpa_kesalahan() {
        val e = AuthValidators.validateRegister("Budi Santoso", "budi@x.com", "081234567890", "Rahasia123", "Rahasia123")
        assertTrue(e.isEmpty())
    }

    @Test fun register_semua_field_salah() {
        val e = AuthValidators.validateRegister("Bu", "x", "123", "pendek", "beda")
        assertEquals(setOf("nama_lengkap", "email", "no_telepon", "password", "konfirmasi_password"), e.keys)
    }

    @Test fun password_harus_ada_huruf_dan_angka() {
        assertTrue("password" in AuthValidators.validateRegister("Budi", "b@x.com", "081234567890", "12345678", "12345678"))
        assertTrue("password" in AuthValidators.validateRegister("Budi", "b@x.com", "081234567890", "abcdefgh", "abcdefgh"))
    }
}