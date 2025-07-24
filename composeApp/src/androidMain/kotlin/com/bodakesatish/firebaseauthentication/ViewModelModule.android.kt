package com.bodakesatish.firebaseauthentication

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val viewModelModule = module {
    viewModelOf(::AuthViewModel)
}