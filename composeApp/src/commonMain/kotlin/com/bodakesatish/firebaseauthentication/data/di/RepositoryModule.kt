package com.bodakesatish.firebaseauthentication.data.di

import com.bodakesatish.firebaseauthentication.data.repository.AuthRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import org.koin.dsl.module

val repositoryModule = module {
    single { // Or factory if preferred
        AuthRepository(
            firebaseAuth = get(), // Tells Koin to resolve FirebaseAuth
            firestore = get(),    // Tells Koin to resolve FirebaseFirestore
            userDao = get()       // Tells Koin to resolve ProfileEntityQueries
        )
    }
}

// You also need modules to provide FirebaseAuth, FirebaseFirestore, and ProfileEntityQueries

val firebaseModule = module {
    single { Firebase.auth }    // Provides dev.gitlive FirebaseAuth instance
    single { Firebase.firestore } // Provides dev.gitlive FirebaseFirestore instance
}
