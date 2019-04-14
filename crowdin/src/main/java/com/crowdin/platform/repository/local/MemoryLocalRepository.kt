package com.crowdin.platform.repository.local

import com.crowdin.platform.repository.SearchResultData
import com.crowdin.platform.repository.remote.api.ArrayData
import com.crowdin.platform.repository.remote.api.LanguageData
import com.crowdin.platform.repository.remote.api.PluralData
import java.util.*

/**
 * A LocalRepository which keeps the stringsData ONLY in memory.
 */
internal class MemoryLocalRepository : LocalRepository {

    private val stringsData = LinkedHashMap<String, LanguageData>()

    override fun saveLanguageData(languageData: LanguageData) {
        when (val data = stringsData[languageData.language]) {
            null -> stringsData[languageData.language] = languageData
            else -> data.updateResources(languageData)
        }
    }

    override fun setString(language: String, key: String, value: String) {
        when (val data = stringsData[language]) {
            null -> {
                val languageData = LanguageData(language)
                languageData.resources[key] = value
                stringsData[language] = languageData
            }
            else -> data.resources[key] = value
        }
    }

    override fun setArrayData(language: String, key: String, arrayData: ArrayData) {
        when (val data = stringsData[language]) {
            null -> {
                val languageData = LanguageData(language)
                languageData.arrays.add(arrayData)
                stringsData[language] = languageData
            }
            else -> {
                var index = -1
                data.arrays.forEachIndexed { position, array ->
                    if (array.name == arrayData.name) {
                        index = position
                    }
                }

                when {
                    index != -1 -> data.arrays[index] = arrayData
                    else -> data.arrays.add(arrayData)
                }
            }
        }
    }

    override fun setPluralData(language: String, key: String, pluralData: PluralData) {
        when (val data = stringsData[language]) {
            null -> {
                val languageData = LanguageData(language)
                languageData.plurals.add(pluralData)
                stringsData[language] = languageData
            }
            else -> {
                var isExist = false
                data.plurals.forEach { plural ->
                    if (plural.name == pluralData.name) {
                        isExist = true
                        pluralData.quantity.forEach {
                            plural.quantity[it.key] = it.value
                        }
                        plural.formatArgs = pluralData.formatArgs
                    }
                }
                if (!isExist) {
                    data.plurals.add(pluralData)
                }
            }
        }
    }

    override fun getString(language: String, key: String): String? {
        val languageData = stringsData[language]
        return when {
            languageData == null || !languageData.resources.containsKey(key) -> null
            else -> languageData.resources[key]
        }
    }

    override fun getLanguageData(language: String): LanguageData? =
            when {
                !stringsData.containsKey(language) -> null
                else -> stringsData[language]
            }

    override fun getStringArray(key: String): Array<String>? {
        stringsData[Locale.getDefault().toString()]?.arrays?.forEach { array ->
            if (array.name == key) {
                return array.values
            }
        }

        return null
    }

    override fun getStringPlural(resourceKey: String, quantityKey: String): String? {
        stringsData[Locale.getDefault().toString()]?.plurals?.forEach { pluralData ->
            if (pluralData.name == resourceKey) {
                return pluralData.quantity[quantityKey]
            }
        }
        return null
    }

    override fun isExist(language: String): Boolean {
        return stringsData[language] != null
    }

    override fun getTextData(text: String): SearchResultData {
        val searchResultData = SearchResultData()

        val languageData = stringsData[Locale.getDefault().toString()]
        searchInResources(languageData, text, searchResultData)

        val languageReserveData = stringsData["${Locale.getDefault().language}-copy"]
        searchInResources(languageReserveData, text, searchResultData)

        return searchResultData
    }

    private fun searchInResources(languageData: LanguageData?, text: String, searchResultData: SearchResultData) {
        searchInStrings(languageData, text, searchResultData)
        searchInArrays(languageData, text, searchResultData)
        searchInPlurals(languageData, text, searchResultData)
    }

    private fun searchInPlurals(languageData: LanguageData?, text: String, searchResultData: SearchResultData) {
        languageData?.plurals?.forEach { pluralData ->
            val pluralName = pluralData.name
            pluralData.quantity.forEach {
                if (it.value == text) {
                    searchResultData.pluralName = pluralName
                    searchResultData.pluralQuantity = pluralData.number
                    searchResultData.pluralFormatArgs = pluralData.formatArgs
                    return
                }
            }
        }
    }

    private fun searchInArrays(languageData: LanguageData?, text: String, searchResultData: SearchResultData) {
        languageData?.arrays?.forEach { arrayData ->
            val arrayName = arrayData.name
            arrayData.values?.forEachIndexed { index, item ->
                if (item == text) {
                    searchResultData.arrayName = arrayName
                    searchResultData.arrayIndex = index
                    return
                }
            }
        }
    }

    private fun searchInStrings(languageData: LanguageData?, text: String, searchResultData: SearchResultData) {
        languageData?.resources?.forEach {
            if (it.value == text) {
                searchResultData.key = it.key
                return
            }
        }
    }
}