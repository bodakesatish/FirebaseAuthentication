package com.bodakesatish.firebaseauthentication.data.source.local.di

import com.bodakesatish.firebaseauthentication.data.source.local.AndroidDatabaseDriverFactory
import com.bodakesatish.firebaseauthentication.data.source.local.factory.DatabaseDriverFactory

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val databaseDriverModule = module {
    single<DatabaseDriverFactory> {
        AndroidDatabaseDriverFactory(androidContext())
    }
}