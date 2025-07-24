package com.bodakesatish.firebaseauthentication.app.di

import com.bodakesatish.firebaseauthentication.app.screens.AuthViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val viewModelModule = module {
    viewModelOf(::AuthViewModel)
}