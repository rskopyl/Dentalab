package com.rskopyl.dentalab.data.preferences

import kotlinx.coroutines.flow.Flow

interface AppPreferencesManager {

    fun get(): Flow<AppPreferences>

    fun updateHomePeriod(period: AppPreferences.HomePeriod)

    fun updateQuickNavigation(isEnabled: Boolean)
}