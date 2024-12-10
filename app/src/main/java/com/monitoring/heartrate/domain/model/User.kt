package com.monitoring.heartrate.domain.model

import java.util.Date

data class User(
    val uid: String,
    val email: String,
    val displayName: String? = null,
    val username: String? = null,
    val photoUrl: String? = null,
    val isEmailVerified: Boolean = false,
    val role: String? = null,
    val creationDate: Date = Date(),
    val token: String? = null // Make token nullable
)


