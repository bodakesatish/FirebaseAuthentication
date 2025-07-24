package com.bodakesatish.firebaseauthentication

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val databaseDriverModule = module {
    single<DatabaseDriverFactory> {
        AndroidDatabaseDriverFactory(androidContext())
    }
}