package com.bodakesatish.firebaseauthentication.data.repository

// com/example/yourapp/data/repository/AuthRepository.kt

//import kotlinx.coroutines.tasks.await
import app.cash.sqldelight.coroutines.asFlow
import com.bodakesatish.firebaseauthentication.ProfileEntity
import com.bodakesatish.firebaseauthentication.ProfileEntityQueries
import com.bodakesatish.firebaseauthentication.domain.model.Profile
import com.bodakesatish.firebaseauthentication.domain.utils.getCurrentEpochMillis
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult() // For UI state
}


class AuthRepository (
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: ProfileEntityQueries
) {

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    // Flow to observe Firebase Auth state
    val authState: Flow<FirebaseUser?> = firebaseAuth.authStateChanged // This is the direct Flow

    suspend fun register(email: String, pass: String, fullName: String): Flow<AuthResult> = flow {
        emit(AuthResult.Loading)
        try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, pass)
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val userFirestore = Profile(
                    uid = firebaseUser.uid,
                    fullName = fullName,
                    email = email,
                    registrationTimestamp = getCurrentEpochMillis()
                )
                // Store user profile in Firestore
                firestore.collection("admins").document(firebaseUser.uid)
                    .set(userFirestore.toHashMap())

                userDao.insertOrReplace( uid = firebaseUser.uid,
                    fullName = fullName,
                    email = email,
                    registrationTimestamp = userFirestore.registrationTimestamp,
                    lastLoginTimestamp = getCurrentEpochMillis()
                ) // Set initial login time)
                emit(AuthResult.Success)
            } else {
                emit(AuthResult.Error("Registration failed: User is null."))
            }
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Registration failed."))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun login(email: String, pass: String): Flow<AuthResult> = flow {
        emit(AuthResult.Loading)
        try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, pass)
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                // Fetch user profile from Firestore and update/insert into Room
                val firestoreDoc = firestore.collection("admins").document(firebaseUser.uid).get()
             //   val userFirestore = firestoreDoc.toObject(UserFirestore::class.java)

                if (firestoreDoc.exists) {
                    // Use the data() method with reified type parameter
                    val userFirestore = firestoreDoc.data<Profile>()

                   val res =  userDao.insertOrReplace( uid = firebaseUser.uid,
                        fullName = userFirestore.fullName,
                        email = userFirestore.email,
                        registrationTimestamp = userFirestore.registrationTimestamp,
                        lastLoginTimestamp = getCurrentEpochMillis()
                   ) // Update last login time) // Using insert with OnConflictStrategy.REPLACE
                } else {
                    emit(AuthResult.Error("User not found: User is null."))
                    // Handle case where user exists in Auth but not Firestore (should ideally not happen if register is correct)
                    // Potentially create a basic profile in Firestore/Room here or log an error
                }
                emit(AuthResult.Success)
            } else {
                emit(AuthResult.Error("Login failed: User is null."))
            }
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Login failed."))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            firebaseAuth.signOut()
//            // Clear local user data (optional, depends on your app's needs for offline access)
            userDao.deleteAll()
//            currentUser?.uid?.let {
//                userDao.deleteUser(it)
//            } // Or userDao.clearAllUsers()
        }
    }

    // Get current logged-in user's profile from Room
    fun getLocalUserProfile(uid: String): Flow<ProfileEntity?> {
        return userDao.selectByCode(uid)
            .asFlow()
            .map { it.executeAsOneOrNull() }
    }

    // Example of simple session time: update last login timestamp in Room
    // This is more of an "last active" than a strict session timeout.
    // For strict session timeout (e.g., auto-logout after X minutes of inactivity),
    // you'd need more complex client-side logic or server-side validation.
    suspend fun updateUserLastLoginTime(uid: String) {
        withContext(Dispatchers.IO) {
            // 1. Execute the query to get the ProfileEntity or null
            val currentUserProfile: ProfileEntity? = userDao.selectByCode(uid).executeAsOneOrNull()

            currentUserProfile?.let { user -> // 2. Check if the user was found
                // 3. Create an updated ProfileEntity with the new lastLoginTimestamp
                val updatedUserProfile = user.copy(
                    lastLoginTimestamp = getCurrentEpochMillis() // Use Clock
                )
                // 4. Call insertOrReplace with the updated entity
                userDao.insertOrReplaceTakingObject(updatedUserProfile)
            }
            // If currentUserProfile is null, it means no user with that uid was found in the local DB.
            // You might want to log this or handle it if it's an unexpected state.
        }
    }
}
