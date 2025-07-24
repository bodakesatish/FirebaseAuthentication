package com.bodakesatish.firebaseauthentication.data.source.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.bodakesatish.firebaseauthentication.AuthDatabase
import com.bodakesatish.firebaseauthentication.data.source.local.factory.DatabaseDriverFactory

class IOSDatabaseDriverFactory(): DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = AuthDatabase.Companion.Schema,
            name = "AuthDatabase.db"
        )
    }
}