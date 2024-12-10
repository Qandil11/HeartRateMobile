package com.monitoring.heartrate.ui.screen.compenets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String = "App Title",
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    onSignOutClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = { onBackClick?.invoke() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            if (onSignOutClick != null) {
                IconButton(onClick = onSignOutClick) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
    )
}
