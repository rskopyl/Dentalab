package com.rskopyl.dentalab.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.rskopyl.dentalab.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppPreferencesManagerImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @ApplicationScope
    private val applicationScope: CoroutineScope
) : AppPreferencesManager {

    override fun get(): Flow<AppPreferences> =
        dataStore.data.map(::mapPreferences)

    override fun updateHomePeriod(period: AppPreferences.HomePeriod) {
        applicationScope.launch(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[Keys.HOME_PERIOD] = period.toString()
            }
        }
    }

    override fun updateQuickNavigation(isEnabled: Boolean) {
        applicationScope.launch(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[Keys.QUICK_NAVIGATION] = isEnabled
            }
        }
    }

    private fun mapPreferences(preferences: Preferences): AppPreferences {
        return AppPreferences(
            homePeriod = preferences[Keys.HOME_PERIOD]
                ?.let(AppPreferences.HomePeriod::valueOf)
                ?: AppPreferences.HomePeriod.DEKADE,
            isQuickNavigationEnabled = preferences[Keys.QUICK_NAVIGATION] ?: false
        )
    }

    private object Keys {

        val HOME_PERIOD = stringPreferencesKey("home_period")
        val QUICK_NAVIGATION = booleanPreferencesKey("quick_navigation")
    }
}