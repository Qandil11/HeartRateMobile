

package com.monitoring.heartrate.domain.network

import com.google.firebase.auth.FirebaseAuth
import com.monitoring.heartrate.data.TokenProvider
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Skip adding Authorization header for login and registration endpoints
        if (request.url.encodedPath.contains("/login") || request.url.encodedPath.contains("/register")) {
            return chain.proceed(request)
        }

        // Fetch the current Firebase ID token or fallback to TokenProvider
        val idToken = runBlocking {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            firebaseUser?.getIdToken(true)?.await()?.token ?: TokenProvider.token
        }

        if (idToken == null) {
            throw IllegalStateException("Token is null. User might not be authenticated.")
        }

        // Add the token to the Authorization header
        val newRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer $idToken")
            .build()

        return chain.proceed(newRequest)
    }
}
