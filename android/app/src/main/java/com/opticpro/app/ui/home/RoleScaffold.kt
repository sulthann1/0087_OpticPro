package com.opticpro.app.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.opticpro.app.ui.common.PlaceholderScreen
import com.opticpro.app.ui.common.ProfileScreen
import com.opticpro.app.ui.navigation.UserSession

data class TabItem(val route: String, val label: String, val icon: ImageVector, val note: String = "")

private const val PROFILE = "profile"

@Composable
private fun RoleScaffold(tabs: List<TabItem>, user: UserSession, onLogout: () -> Unit) {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val current = backStack?.destination?.route
    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = current == tab.route,
                        onClick = {
                            nav.navigate(tab.route) {
                                popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(nav, startDestination = tabs.first().route, modifier = Modifier.padding(padding)) {
            tabs.forEach { tab ->
                composable(tab.route) {
                    if (tab.route == PROFILE) ProfileScreen(user, onLogout) else PlaceholderScreen(tab.label, tab.note)
                }
            }
        }
    }
}

@Composable
fun CustomerHome(user: UserSession, onLogout: () -> Unit) = RoleScaffold(
    tabs = listOf(
        TabItem("katalog", "Katalog", Icons.Default.Search, "Dikerjakan pada Minggu 2"),
        TabItem("resep", "Resep", Icons.Default.Info, "Dikerjakan pada Minggu 2"),
        TabItem("pesanan", "Pesanan", Icons.AutoMirrored.Filled.List, "Dikerjakan pada Minggu 3"),
        TabItem("jadwal", "Jadwal", Icons.Default.DateRange, "Dikerjakan pada Minggu 4"),
        TabItem(PROFILE, "Profil", Icons.Default.Person),
    ),
    user = user,
    onLogout = onLogout,
)

@Composable
fun AdminHome(user: UserSession, onLogout: () -> Unit) = RoleScaffold(
    tabs = listOf(
        TabItem("dashboard", "Dashboard", Icons.Default.Home, "Dikerjakan pada Minggu 5"),
        TabItem("katalog", "Katalog", Icons.Default.Search, "Dikerjakan pada Minggu 2"),
        TabItem("resep", "Resep", Icons.Default.Info, "Dikerjakan pada Minggu 2"),
        TabItem("pesanan", "Pesanan", Icons.AutoMirrored.Filled.List, "Dikerjakan pada Minggu 3"),
        TabItem(PROFILE, "Lainnya", Icons.Default.Menu),
    ),
    user = user,
    onLogout = onLogout,
)