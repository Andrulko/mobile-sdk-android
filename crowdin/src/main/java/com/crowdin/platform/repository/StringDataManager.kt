package com.crowdin.platform.repository

import android.content.Context
import com.crowdin.platform.LocalDataChangeObserver
import com.crowdin.platform.repository.local.LocalRepository
import com.crowdin.platform.repository.remote.Connectivity
import com.crowdin.platform.repository.remote.NetworkType
import com.crowdin.platform.repository.remote.RemoteRepository
import com.crowdin.platform.repository.remote.api.ArrayData
import com.crowdin.platform.repository.remote.api.LanguageData
import com.crowdin.platform.repository.remote.api.PluralData
import com.crowdin.platform.utils.FeatureFlags
import com.crowdin.platform.utils.ThreadUtils
import java.util.*

internal class StringDataManager(private val remoteRepository: RemoteRepository,
                                 private val localRepository: LocalRepository,
                                 private val dataChangeObserver: LocalDataChangeObserver) : TextIdProvider {

    override fun provideTextKey(text: String): SearchResultData {
        return localRepository.getTextData(text)
    }

    fun getString(language: String, stringKey: String): String? {
        return localRepository.getString(language, stringKey)
    }

    fun setString(language: String, key: String, value: String) {
        localRepository.setString(language, key, value)
    }

    fun getStringArray(key: String): Array<String>? {
        return localRepository.getStringArray(key)
    }

    fun getStringPlural(resourceKey: String, quantityKey: String): String? {
        return localRepository.getStringPlural(resourceKey, quantityKey)
    }

    fun updateData(context: Context, networkType: NetworkType) {
        if (Connectivity.isOnline(context) && Connectivity.isNetworkAllowed(context, networkType)) {
            ThreadUtils.runInBackgroundPool(Runnable {
                remoteRepository.fetchData(object : LanguageDataCallback {

                    override fun onDataLoaded(languageData: LanguageData) {
                        localRepository.saveLanguageData(languageData)
                        if (FeatureFlags.isRealTimeUpdateEnabled) {
                            dataChangeObserver.onDataChanged()
                        }
                    }
                })
            }, false)
        }
    }

    fun saveReserveResources(stringKey: String, defaultText: String = "",
                             arrayData: ArrayData? = null,
                             pluralData: PluralData? = null) {
        if (FeatureFlags.isRealTimeUpdateEnabled) {
            when {
                defaultText.isNotEmpty() -> localRepository.setString("${Locale.getDefault().language}-copy", stringKey, defaultText)
                arrayData != null -> localRepository.setArrayData("${Locale.getDefault().language}-copy", stringKey, arrayData)
                pluralData != null -> localRepository.setPluralData("${Locale.getDefault().language}-copy", stringKey, pluralData)
            }
        }
    }
}

internal interface TextIdProvider {

    fun provideTextKey(text: String): SearchResultData
}
