package com.monitoring.heartrate.ui.screen.compenets

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.monitoring.heartrate.domain.model.Response

@Composable
 fun DisplayError(loginResult: Response<Any>) {
    if (loginResult is Response.Failure) {
        val context = LocalContext.current // Get the current Compose context
        var text = (loginResult as Response.Failure).exception.message ?: "Unknown error"

        Toast.makeText(context, text ?: "Unknown error", Toast.LENGTH_LONG).show()

    }
}
