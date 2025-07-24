package com.bodakesatish.firebaseauthentication.data.source.local.di

import com.bodakesatish.firebaseauthentication.data.source.local.IOSDatabaseDriverFactory
import com.bodakesatish.firebaseauthentication.data.source.local.factory.DatabaseDriverFactory
import org.koin.dsl.module

actual val databaseDriverModule = module {
    single<DatabaseDriverFactory> {
        IOSDatabaseDriverFactory()
    }
}