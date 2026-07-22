package com.eduteleaccess.customer.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val TOKEN = stringPreferencesKey("token")
        val CUSTOMER_ID = stringPreferencesKey("customer_id")
        val FULL_NAME = stringPreferencesKey("full_name")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    suspend fun saveUser(token: String, customerId: String, full_name:String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN] = token
            prefs[CUSTOMER_ID] = customerId
            prefs[FULL_NAME] = full_name
            prefs[IS_LOGGED_IN] = true
        }
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map {
        it[TOKEN]
    }

    val usernameFlow: Flow<String?> = context.dataStore.data.map {
        it[FULL_NAME]
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map {
        it[IS_LOGGED_IN] ?: false
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}