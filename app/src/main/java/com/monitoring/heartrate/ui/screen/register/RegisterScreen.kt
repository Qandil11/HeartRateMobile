
package com.monitoring.heartrate.ui.screen.register

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.monitoring.heartrate.domain.model.Response
import com.monitoring.heartrate.ui.screen.compenets.DisplayError
import com.monitoring.heartrate.ui.screen.compenets.EmailAndPasswordInputs
import com.monitoring.heartrate.ui.screen.compenets.NavigationText
import com.monitoring.heartrate.util.Button.LoadingButton
import com.monitoring.heartrate.util.image.AsyncImageProfile
import androidx.compose.foundation.rememberScrollState
import androidx.hilt.navigation.compose.hiltViewModel
import com.monitoring.heartrate.ui.UserDataViewModel

@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel = hiltViewModel(),
    navController: NavController,
    userDataViewModel: UserDataViewModel

) {


    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var photoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val registrationResult by registerViewModel.registrationState.collectAsState()
    val scrollState = rememberScrollState()

    // Image picker launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Heart Rate App",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Please fill in the details below to create an account",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (photoUri != null) {
                    AsyncImageProfile(photoUrl = photoUri.toString())
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "No photo selected",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(80.dp)
                            .border(2.dp, Color.Yellow, CircleShape)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Select Photo")
                }
                if (photoUri != null) {
                    Button(
                        onClick = { photoUri = null },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Clear")
                    }
                }
            }

            EmailAndPasswordInputs(
                name = name,
                onNameChange = { name = it },
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                isError = registrationResult is Response.Failure,
                showNameField = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            DisplayError(registrationResult)

            LoadingButton(
                text = "Register",
                isLoading = registerViewModel.isLoading,
                enabled = !(email.isBlank() || password.isBlank() || name.isBlank() || photoUri == null),
                textloading = "Registering...",
                onClick = {
                    registerViewModel.register(email, password, name, photoUri.toString(), navController, userDataViewModel)
                }
            )

            NavigationText(
                text = "Already have an account? Login",
                onClick = {
                    navController.navigate("login")
                }
            )
        }
    }
}
