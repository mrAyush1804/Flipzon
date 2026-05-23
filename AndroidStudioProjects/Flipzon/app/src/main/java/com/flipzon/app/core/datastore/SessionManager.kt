package com.flipzon.app.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

data class SessionData(
    val userId: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val imageUrl: String
)

class SessionManager(private val context: Context) {

    companion object {
        private val USER_ID = intPreferencesKey("user_id")
        private val EMAIL = stringPreferencesKey("email")
        private val FIRST_NAME = stringPreferencesKey("first_name")
        private val LAST_NAME = stringPreferencesKey("last_name")
        private val IMAGE_URL = stringPreferencesKey("image_url")
    }

    suspend fun saveSession(
        userId: Int,
        email: String,
        firstName: String,
        lastName: String,
        imageUrl: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[EMAIL] = email
            preferences[FIRST_NAME] = firstName
            preferences[LAST_NAME] = lastName
            preferences[IMAGE_URL] = imageUrl
        }
    }

    val sessionFlow: Flow<SessionData?> = context.dataStore.data.map { preferences ->
        val userId = preferences[USER_ID]
        if (userId != null) {
            SessionData(
                userId = userId,
                email = preferences[EMAIL] ?: "",
                firstName = preferences[FIRST_NAME] ?: "",
                lastName = preferences[LAST_NAME] ?: "",
                imageUrl = preferences[IMAGE_URL] ?: ""
            )
        } else {
            null
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
