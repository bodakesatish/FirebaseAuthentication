package com.bodakesatish.firebaseauthentication.app.di

import com.bodakesatish.firebaseauthentication.data.source.local.di.databaseDriverModule
import com.bodakesatish.firebaseauthentication.data.source.local.di.databaseModule
import com.bodakesatish.firebaseauthentication.data.di.dispatchersModule
import com.bodakesatish.firebaseauthentication.data.di.firebaseModule
import com.bodakesatish.firebaseauthentication.data.di.repositoryModule

val appModules = listOf(
    dispatchersModule,
    databaseDriverModule,
    databaseModule,
    viewModelModule,
    repositoryModule,
    firebaseModule
)
