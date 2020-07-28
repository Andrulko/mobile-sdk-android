package com.crowdin.platform.data.remote

import android.util.Log
import com.crowdin.platform.Crowdin
import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.LanguageData
import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.parser.ReaderFactory
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import com.crowdin.platform.util.ThreadUtils
import com.crowdin.platform.util.executeIO
import okhttp3.ResponseBody
import retrofit2.Response
import java.net.HttpURLConnection
import java.util.Locale

private const val XML_EXTENSION = ".xml"

internal class StringDataRemoteRepository(
    private val crowdinDistributionApi: CrowdinDistributionApi,
    private val distributionHash: String
) : CrowdingRepository(
    crowdinDistributionApi,
    distributionHash = distributionHash
) {

    private var preferredLanguageCode: String? = null

    override fun fetchData(languageCode: String?, languageDataCallback: LanguageDataCallback?) {
        preferredLanguageCode = languageCode
        getManifest(languageDataCallback)
    }

    override fun onManifestDataReceived(
        manifest: ManifestData?,
        languageDataCallback: LanguageDataCallback?
    ) {
        val supportedLanguages = manifest?.languages
        if (preferredLanguageCode == null) {
            preferredLanguageCode = getMatchedCode(supportedLanguages) ?: return
        } else {
            if (supportedLanguages?.contains(preferredLanguageCode!!) == false) {
                return
            }
        }

        // Combine all data before save to storage
        val languageData = LanguageData()
        val languageInfo = getLanguageInfo(preferredLanguageCode!!)?.data
        languageInfo?.let { info ->
            languageData.language = info.locale
            manifest?.files?.forEach {
                val filePath = validateFilePath(it, info, preferredLanguageCode!!)
                val eTag = eTagMap[filePath]
                val result = requestStringData(
                    eTag,
                    distributionHash,
                    filePath,
                    manifest.timestamp,
                    languageDataCallback
                )
                languageData.addNewResources(result)
            }

            ThreadUtils.executeOnMain { languageDataCallback?.onDataLoaded(languageData) }
        }
    }

    private fun requestStringData(
        eTag: String?,
        distributionHash: String,
        filePath: String,
        timestamp: Long,
        languageDataCallback: LanguageDataCallback?
    ): LanguageData {
        var languageData = LanguageData()
        var result: Response<ResponseBody>? = null

        executeIO {
            result = crowdinDistributionApi.getResourceFile(
                eTag ?: HEADER_ETAG_EMPTY,
                distributionHash,
                filePath,
                timestamp
            ).execute()
        }

        result?.let {
            val body = it.body()
            val code = it.code()
            when {
                code == HttpURLConnection.HTTP_OK && body != null -> {
                    languageData = onStringDataReceived(
                        it.headers()[HEADER_ETAG],
                        filePath,
                        body
                    )
                }
                code == HttpURLConnection.HTTP_FORBIDDEN -> {
                    val errorMessage =
                        "Translation file $filePath for locale $preferredLanguageCode not found in the distribution"
                    Log.i(Crowdin.CROWDIN_TAG, errorMessage)
                    languageDataCallback?.onFailure(Throwable(errorMessage))
                }
                code != HttpURLConnection.HTTP_NOT_MODIFIED ->
                    languageDataCallback?.onFailure(Throwable("Unexpected http error code $code"))
                else -> {
                }
            }
        }

        return languageData
    }

    private fun onStringDataReceived(
        eTag: String?,
        filePath: String,
        body: ResponseBody
    ): LanguageData {
        eTag?.let { eTagMap.put(filePath, eTag) }

        val extension = if (filePath.contains(XML_EXTENSION)) {
            ReaderFactory.ReaderType.XML
        } else {
            ReaderFactory.ReaderType.JSON
        }
        val reader = ReaderFactory.createReader(extension)
        return reader.parseInput(body.byteStream())
    }
}
