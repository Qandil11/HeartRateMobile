package com.monitoring.heartrate.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.monitoring.heartrate.ui.screen.dashboard.DashboardScreen
import com.monitoring.heartrate.ui.screen.dashboard.DashboardViewModel
import com.monitoring.heartrate.ui.screen.healthreport.HealthReportScreen
import com.monitoring.heartrate.ui.screen.healthreport.HealthReportViewModel
import com.monitoring.heartrate.ui.screen.heartRateTrends.HeartRateTrendsScreen
import com.monitoring.heartrate.ui.screen.heartRateTrends.TrendsViewModel
import com.monitoring.heartrate.ui.screen.login.LoginScreen
import com.monitoring.heartrate.ui.screen.login.LoginViewModel
import com.monitoring.heartrate.ui.screen.profile.EditProfileScreen
import com.monitoring.heartrate.ui.screen.profile.ProfileScreen
import com.monitoring.heartrate.ui.screen.register.RegisterScreen
import com.monitoring.heartrate.ui.screen.register.RegisterViewModel
import com.monitoring.heartrate.ui.screen.threshhold.ThresholdManagementScreen
import com.monitoring.heartrate.ui.screen.threshhold.ThresholdViewModel
import com.monitoring.heartrate.ui.theme.HeartMonitorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            HeartMonitorTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val userDataViewModel: UserDataViewModel = hiltViewModel()
                    MainScreen(userDataViewModel = userDataViewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreen(userDataViewModel: UserDataViewModel) {
    val navController = rememberNavController()
    val registerViewModel: RegisterViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val dashboardViewModel: DashboardViewModel = hiltViewModel()
    val trendsViewModel: TrendsViewModel = hiltViewModel()
    val healthReportViewModel: HealthReportViewModel = hiltViewModel()
        val thresholdViewModel: ThresholdViewModel = hiltViewModel()

    // Check authentication state when the MainScreen is recomposed
    LaunchedEffect(Unit) {
        checkAuthenticationState(navController, userDataViewModel)
    }

    NavHost(
        navController = navController,
        startDestination = "splash" // Set the splash screen as the initial destination
    ) {
        composable("splash") {
            SplashScreen()
        }
        composable("loading") {
            LoadingScreen()
        }
        composable("login") {
            LoginScreen(
                navController = navController,
                loginViewModel = loginViewModel,
                userDataViewModel = userDataViewModel
            )
        }
        composable("register") {
            RegisterScreen(
                navController = navController,
                registerViewModel = registerViewModel,
                userDataViewModel = userDataViewModel
            )
        }
        composable("dashboard") {
            DashboardScreen(
                dashboardViewModel = dashboardViewModel,
                navController = navController,
                userDataViewModel = userDataViewModel
            )
        }
        composable("profile") {
            ProfileScreen(
                navController = navController,
                userDataViewModel = userDataViewModel
            )
        }
        composable("edit_profile") {
            EditProfileScreen(
                navController = navController,
                userDataViewModel = userDataViewModel
            )
        }
        composable("trends") { backStackEntry ->
            HeartRateTrendsScreen(
                viewModel = trendsViewModel,
                navController = navController,
                userDataViewModel = userDataViewModel
            )
        }
        composable("threshold_management") {
            ThresholdManagementScreen(navController, userDataViewModel, thresholdViewModel)
        }

        composable("health_report") {
            HealthReportScreen(
                navController = navController,
                userDataViewModel = userDataViewModel,
                trendsViewModel = trendsViewModel,
                viewModel = healthReportViewModel
            )
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Heart Rate Monitor",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp // Add this parameter to avoid potential issues
            )        }
    }
}

private fun checkAuthenticationState(navController: NavController, userDataViewModel: UserDataViewModel) {
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    if (currentUser != null) {
        // Get user data from Firebase
        userDataViewModel.getUserData()

        // Navigate to the DashboardScreen instead of ProfileScreen
        navController.navigate("dashboard") {
            popUpTo("splash") { inclusive = true }
            launchSingleTop = true // Prevent multiple instances of the same destination
        }
    } else {
        // Navigate to the LoginScreen
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
            launchSingleTop = true // Prevent multiple instances of the same destination
        }
    }
}
