package com.bodakesatish.firebaseauthentication.data.source.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.bodakesatish.firebaseauthentication.AuthDatabase
import com.bodakesatish.firebaseauthentication.data.source.local.factory.DatabaseDriverFactory

class AndroidDatabaseDriverFactory(
    private val context: Context
): DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = AuthDatabase.Companion.Schema,
            context = context,
            name = "AuthDatabase.db"
        )
    }
}