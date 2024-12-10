package com.monitoring.heartrate.data

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.monitoring.heartrate.domain.model.Response
import com.monitoring.heartrate.domain.model.User
import com.monitoring.heartrate.domain.repository.AuthRepository
import com.monitoring.heartrate.util.valid.validateEmailAndPassword
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.monitoring.heartrate.domain.model.LoginRequest
import com.monitoring.heartrate.domain.model.RegisterRequest
import com.monitoring.heartrate.domain.model.toUserModel
import com.monitoring.heartrate.domain.network.RetrofitBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

import kotlinx.coroutines.flow.single
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {
    private val auth = Firebase.auth
    private val storage = FirebaseStorage.getInstance()

    // Expose current Firebase user globally
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun registerUser(
        email: String,
        password: String,
        displayName: String,
        photoUri: String
    ): Flow<Response<User>> = flow {
        try {
            val auth = FirebaseAuth.getInstance()

            // Create user with Firebase Authentication
            val user = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = user.user ?: throw Exception("User creation failed")
            val idTokenResult = firebaseUser.getIdToken(true).await()
            val idToken = idTokenResult.token ?: throw Exception("Failed to retrieve ID token")
            // Set the token in TokenProvider
            TokenProvider.token = idToken
            // Update profile with displayName and photoUri
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(photoUri.toUri())
                .build()

            firebaseUser.updateProfile(profileUpdates).await()

            // Emit success without the token
            val userModel = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = displayName,
                username = null,
                photoUrl = photoUri,
                isEmailVerified = firebaseUser.isEmailVerified,
                role = null,
                creationDate = Date(),
                token = null // Token will be fetched later during login
            )
            emit(Response.Success(userModel))
        } catch (e: Exception) {
            emit(Response.Failure(Exception("Registration failed: ${e.localizedMessage}", e)))
        }
    }.flowOn(Dispatchers.IO)





    override suspend fun loginUser(
        email: String,
        password: String
    ): Flow<Response<User>> = flow {
        try {
            val auth = FirebaseAuth.getInstance()

            // Sign in with Firebase Authentication
            val user = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = user.user ?: throw Exception("User login failed")

            val idTokenResult = firebaseUser.getIdToken(true).await()
            val idToken = idTokenResult.token ?: throw Exception("Failed to retrieve ID token")
            // Set the token in TokenProvider
            TokenProvider.token = idToken

            // Emit success
            val userModel = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName,
                username = null, // Firebase Authentication doesn't provide username
                photoUrl = firebaseUser.photoUrl?.toString(),
                isEmailVerified = firebaseUser.isEmailVerified,
                role = null, // Not applicable since we're not using Firestore
                creationDate = Date(firebaseUser.metadata?.creationTimestamp ?: System.currentTimeMillis()),
                token = "" // Token generation isn't required since we aren't using server verification
            )
            emit(Response.Success(userModel))
        } catch (e: Exception) {
            emit(Response.Failure(Exception("Login failed: ${e.localizedMessage}", e)))
        }
    }.flowOn(Dispatchers.IO)



    private fun getUserDataFromFirebase(): User {
        val firebaseUser = auth.currentUser
        val uid = firebaseUser?.uid ?: throw Exception("User is not logged in")

        val email = firebaseUser.email ?: ""
        val displayName = firebaseUser.displayName ?: ""
        val photoUrl = downloadImageFromFirebaseStorage(firebaseUser.photoUrl?.toString() ?: "")
        val isEmailVerified = firebaseUser.isEmailVerified

        return User(uid, email, displayName, photoUrl,"", isEmailVerified, "", Date(), "")
    }


    private fun downloadImageFromFirebaseStorage(photoUrl: String?): String {
        return try {
            // Ensure that the photoUrl is a full download URL
            if (photoUrl != null && photoUrl.startsWith("https://")) {
                // If it's a Firebase Storage URL, use it directly
                photoUrl

            } else {
                // Handle other cases or log an error
                Log.e("AuthRepositoryImpl", "Invalid photoUrl format: $photoUrl")
                ""
            }
        } catch (e: Exception) {
            // Handle the exception, e.g., log it or throw a custom exception
            Log.e("AuthRepositoryImpl", "Error processing photoUrl: $photoUrl. Error: ${e.message}")
            ""
        }
    }


    override suspend fun getUserData(): Response<User> = flow {
        try {
            val user = getUserDataFromFirebase()
            emit(Response.Success(user))
            Log.d("response", "getUserData: $user")
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }.flowOn(Dispatchers.IO).single()

    private suspend fun uploadImageToFirebaseStorage(photoUri: Uri): String {
        return try {
            // Create a reference to the image file in Firebase Storage
            val storageRef = storage.reference
            val imageRef = storageRef.child("profile_images/${photoUri.lastPathSegment}")

            // Upload the image to Firebase Storage
            imageRef.putFile(photoUri).await()

            // Get the download URL
            val downloadUrl = imageRef.downloadUrl.await()

            downloadUrl.toString()
        } catch (e: Exception) {
            // Handle the exception, e.g., log it or throw a custom exception
            Log.e("AuthRepositoryImpl", "Error uploading image to Firebase Storage: ${e.message}")
            ""
        }
    }

    // In AuthRepository

    override suspend fun updateUserProfile(
        uid: String, displayName: String, photoUri: String
    ): Response<User> = flow {
        try {
            if (displayName.isBlank()) {
                emit(Response.Failure(Exception("Display name cannot be empty")))
                return@flow
            }

            // Fetch the current user's data
            val currentUser = getUserDataFromFirebase()

            // Delete the previous image from Firebase Storage
            currentUser.photoUrl?.let { deleteImageFromFirebaseStorage(it) }

            // Upload the new image to Firebase Storage
            val uploadedPhotoUrl = uploadImageToFirebaseStorage(Uri.parse(photoUri))

            if (uploadedPhotoUrl.isEmpty()) {
                emit(Response.Failure(Exception("Failed to upload image to Firebase Storage")))
                return@flow
            }

            // Update the user's profile with the new image URL
            val user = auth.currentUser
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(displayName)
                .setPhotoUri(Uri.parse(uploadedPhotoUrl)).build()

            user?.updateProfile(profileUpdates)?.await()

            // Fetch the updated user data from Firebase
            val updatedUser = getUserDataFromFirebase()
            emit(Response.Success(updatedUser))
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }.flowOn(Dispatchers.IO).single()


    private suspend fun deleteImageFromFirebaseStorage(photoUrl: String): Response<Unit> {
        return try {
            if (photoUrl.isNotEmpty() && photoUrl.startsWith("https://")) {
                val storageRef = storage.getReferenceFromUrl(photoUrl)
                storageRef.delete().await()
                Response.Success(Unit)
            } else {
                Response.Failure(Exception("Invalid photoUrl format"))
            }
        } catch (e: Exception) {
            // Handle the exception, e.g., log it or throw a custom exception
            Response.Failure(e)
        }
    }

    override suspend fun sendEmailVerification(): Response<Unit> = flow {
        try {
            val user = auth.currentUser
            // If the user is already verified, don't send email again
            if (user?.isEmailVerified == true) {
                emit(Response.Failure(Exception("Your email is already verified")))
            } else {
                user?.sendEmailVerification()?.await()
                emit(Response.Success(Unit))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }.flowOn(Dispatchers.IO).single()



    override suspend fun markEmailAsVerified(): Response<Unit> = flow {
        try {
            val user = auth.currentUser
            user?.reload()?.await() // Reload the user data to get the latest email verification status
            val updatedUser = auth.currentUser // Retrieve the user again to get the updated status

            if (updatedUser?.isEmailVerified == true) {
                emit(Response.Success(Unit))
            } else {
                // If the user is not verified, send email again and show error message
                user?.sendEmailVerification()?.await()
                emit(Response.Failure(Exception("Your email is not verified. We have sent you another email. Please verify your email")))



            }
        } catch (e: Exception) {
            emit(Response.Failure(e))

        }
    }.flowOn(Dispatchers.IO).single()




    override suspend fun logout(): Response<Unit> = flow {
        try {
            auth.signOut()
            emit(Response.Success(Unit))
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }.flowOn(Dispatchers.IO).single()
}


















