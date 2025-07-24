package com.bodakesatish.firebaseauthentication

import android.app.Application

class AuthApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KoinInitializer(applicationContext).init()
    }
}