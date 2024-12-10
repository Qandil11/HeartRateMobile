package com.monitoring.heartrate.domain.model

import java.util.Date

class ModelConversions {
    // File: ModelConversions.kt in the package where User and UserDTO are defined

    fun UserDTO.toUserModel(): User {
        return User(
            uid = this.uid,
            email = this.email,
            displayName = this.displayName,
            username = this.username,
            photoUrl = this.photoUrl,
            isEmailVerified = this.isEmailVerified,
            role = this.role,
            creationDate = Date(this.creationDate * 1000), // Assuming the creationDate is a Unix timestamp in seconds
            token = this.token
        )
    }

}