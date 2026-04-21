package com.studiocar.studio.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Gerencia persistência de estados simples do app.
 */
class TutorialManager(private val context: Context) {
    companion object {
        private val HAS_SEEN_TUTORIAL = booleanPreferencesKey("has_seen_tutorial")
    }

    val hasSeenTutorial: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[HAS_SEEN_TUTORIAL] ?: false
    }

    suspend fun setTutorialSeen(seen: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HAS_SEEN_TUTORIAL] = seen
        }
    }
}



