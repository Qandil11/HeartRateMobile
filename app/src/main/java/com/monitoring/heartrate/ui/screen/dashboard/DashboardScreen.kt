package com.monitoring.heartrate.ui.screen.dashboard
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.monitoring.heartrate.ui.UserDataViewModel
import com.monitoring.heartrate.ui.screen.compenets.AppTopBar

@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel,
    navController: NavController,
    userDataViewModel: UserDataViewModel
) {
    val user by userDataViewModel.user.collectAsState()
    LaunchedEffect(user) {
        user?.let {
            dashboardViewModel.setUserId(it.uid) // Pass userId to ViewModel
            dashboardViewModel.fetchDashboardData() // Fetch dashboard data
        }
    }

    // Observe the state
    val dashboardState by dashboardViewModel.dashboardData.collectAsState()
    val isLoading by dashboardViewModel.isLoading.collectAsState()
    val errorMessage by dashboardViewModel.errorMessage.collectAsState()

    val context = LocalContext.current

    BackHandler {
        if (context is Activity) {
            context.finish() // Close the app
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Dashboard",
                onSignOutClick = { userDataViewModel.logout(navController) }
            )
        },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when {
                    isLoading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                    errorMessage != null -> Text("Error: $errorMessage")
                    dashboardState != null -> {
                        DashboardContent(
                            data = dashboardState!!,
                            navController = navController,
                            signOut = { userDataViewModel.logout(navController) }
                        )
                    }
                }
            }
        }
    )
}
