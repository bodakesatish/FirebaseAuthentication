package com.bodakesatish.firebaseauthentication

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dispatchersModule = module {

    single {
        Dispatchers.Default
    }

    single<CoroutineDispatcher>(qualifier = named(name = "IODispatcher")) {
        Dispatchers.IO
    }

    single {
        Dispatchers.Main
    }

}