package com.bodakesatish.firebaseauthentication

import com.bodakesatish.firebaseauthentication.app.di.appModules
import org.koin.core.context.startKoin

actual class KoinInitializer {
    actual fun init() {
        startKoin {
            modules(
                appModules
            )
        }
    }
}