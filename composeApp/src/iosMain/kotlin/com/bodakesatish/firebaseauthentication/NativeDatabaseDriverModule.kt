package com.bodakesatish.firebaseauthentication

import org.koin.dsl.module

actual val databaseDriverModule = module {
    single<DatabaseDriverFactory> {
        IOSDatabaseDriverFactory()
    }
}