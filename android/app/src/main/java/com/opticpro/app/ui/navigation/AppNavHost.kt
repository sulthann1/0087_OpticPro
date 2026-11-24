package com.opticpro.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.opticpro.app.ui.auth.LoginScreen
import com.opticpro.app.ui.auth.RegisterScreen
import com.opticpro.app.ui.home.AdminHome
import com.opticpro.app.ui.home.CustomerHome

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val CUSTOMER_HOME = "customer_home"
    const val ADMIN_HOME = "admin_home"
    const val KEY_REGISTERED = "registered"
}

@Composable
fun AppNavHost(vm: AppViewModel = hiltViewModel()) {
    val nav = rememberNavController()
    val state by vm.sessionState.collectAsStateWithLifecycle()

    // Belum login -> Login; sudah login -> beranda sesuai peran
    LaunchedEffect(state) {
        val target = when (val s = state) {
            SessionState.Loading -> null
            SessionState.LoggedOut -> Routes.LOGIN
            is SessionState.LoggedIn -> if (s.user.isAdmin) Routes.ADMIN_HOME else Routes.CUSTOMER_HOME
        }
        if (target != null) nav.navigate(target) { popUpTo(nav.graph.id) { inclusive = true } }
    }

    NavHost(navController = nav, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        }
        composable(Routes.LOGIN) { entry ->
            val registered by entry.savedStateHandle
                .getStateFlow(Routes.KEY_REGISTERED, false).collectAsStateWithLifecycle()
            LoginScreen(justRegistered = registered, onRegisterClick = { nav.navigate(Routes.REGISTER) })
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegistered = {
                    nav.previousBackStackEntry?.savedStateHandle?.set(Routes.KEY_REGISTERED, true)
                    nav.popBackStack()
                },
                onBackToLogin = { nav.popBackStack() },
            )
        }
        composable(Routes.CUSTOMER_HOME) {
            (state as? SessionState.LoggedIn)?.let { CustomerHome(it.user, onLogout = vm::logout) }
        }
        composable(Routes.ADMIN_HOME) {
            (state as? SessionState.LoggedIn)?.let { AdminHome(it.user, onLogout = vm::logout) }
        }
    }
}