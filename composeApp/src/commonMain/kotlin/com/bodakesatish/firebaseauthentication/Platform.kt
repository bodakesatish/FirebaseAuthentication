package com.bodakesatish.firebaseauthentication

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform