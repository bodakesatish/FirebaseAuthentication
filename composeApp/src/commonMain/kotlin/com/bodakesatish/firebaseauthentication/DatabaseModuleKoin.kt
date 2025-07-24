package com.bodakesatish.firebaseauthentication

import org.koin.dsl.module

/**
 * Koin module for providing database-related dependencies.
 */
val databaseModule = module {

    // Provide the AppDatabase instance
    single<AuthDatabase> {
        val driverFactory = get<DatabaseDriverFactory>() // Get the factory
        AuthDatabase(driver = driverFactory.createDriver()) // 'get()' resolves SqlDriver
    }

    // Provide the SchemeEntityQueries (for interacting with the SchemeEntity table)
    single<ProfileEntityQueries> {
        val database = get<AuthDatabase>() // Get the AppDatabase instance
        database.profileEntityQueries // Access the generated queries property
    }
}