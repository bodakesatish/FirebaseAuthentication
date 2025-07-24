package com.bodakesatish.firebaseauthentication.data.source.local.factory

import app.cash.sqldelight.db.SqlDriver

interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}