package com.bodakesatish.firebaseauthentication

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController(
    configure = {
        KoinInitializer().init()
    }
) {
    _root_ide_package_.com.bodakesatish.firebaseauthentication.app.navigation.AppNavigation()
}