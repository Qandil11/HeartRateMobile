package com.monitoring.heartrate.domain.model

import java.util.Date

// Assuming UserDTO is a simpler version received from network responses
data class UserDTO(
    val uid: String,
    val email: String,
    val displayName: String?,
    val username: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean,
    val role: String?,
    val creationDate: Long ,
    val token: String
)

fun UserDTO.toUserModel(): User {
    return User(
        uid = this.uid,
        email = this.email ?: throw IllegalArgumentException("Email cannot be null"),
        displayName = this.displayName, // Nullable field
        username = this.username, // Nullable field
        photoUrl = this.photoUrl, // Nullable field
        isEmailVerified = this.isEmailVerified ?: false,
        role = this.role, // Nullable field
        creationDate = Date(this.creationDate ?: System.currentTimeMillis()), // Default to current time if missing
        token = this.token // Allow null
    )
}
