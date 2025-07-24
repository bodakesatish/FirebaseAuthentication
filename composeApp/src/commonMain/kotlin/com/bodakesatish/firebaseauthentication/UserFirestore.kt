package com.bodakesatish.firebaseauthentication

import kotlinx.serialization.Serializable


@Serializable
data class UserFirestore constructor(
    val uid: String = "", // Matches Firebase Auth UID
    val fullName: String = "",
    val email: String = "",
    // Add other fields you want to store in Firestore, e.g., profileImageUrl, registrationDate
    val registrationTimestamp: Long = 0L
) {
    // No-argument constructor for Firestore deserialization
    constructor() : this("", "", "", 0L)

    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "uid" to uid,
            "fullName" to fullName,
            "email" to email,
            "registrationTimestamp" to registrationTimestamp
        )
    }
}