package com.monitoring.heartrate.domain.model

/**
 * Data class for holding registration request data.
 * @param email User's email address.
 * @param password User's chosen password.
 * @param displayName User's display name.
 * @param photoUri URI of the user's profile photo.
 */
data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String,
    val photoUri: String
)
