package com.crowdin.platform.data.remote

import androidx.annotation.WorkerThread
import com.crowdin.platform.data.LanguageDataCallback
import com.crowdin.platform.data.model.LanguageInfoResponse
import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.remote.api.CrowdinApi
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import com.crowdin.platform.util.ThreadUtils
import com.crowdin.platform.util.executeIO
import java.net.HttpURLConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal abstract class CrowdingRepository(
    private val crowdinDistributionApi: CrowdinDistributionApi,
    private val crowdinApi: CrowdinApi? = null,
    private val distributionHash: String
) : BaseRepository() {

    fun getManifest(languageDataCallback: LanguageDataCallback?) {
        crowdinDistributionApi.getResourceManifest(distributionHash)
            .enqueue(object : Callback<ManifestData> {

                override fun onResponse(
                    call: Call<ManifestData>,
                    response: Response<ManifestData>
                ) {
                    val body = response.body()
                    when {
                        response.code() == HttpURLConnection.HTTP_OK && body != null -> {
                            try {
                                ThreadUtils.runInBackgroundPool(Runnable {
                                    onManifestDataReceived(body, languageDataCallback)
                                }, true)
                            } catch (throwable: Throwable) {
                                languageDataCallback?.onFailure(throwable)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ManifestData>, throwable: Throwable) {
                    languageDataCallback?.onFailure(throwable)
                }
            })
    }

    @WorkerThread
    abstract fun onManifestDataReceived(
        manifest: ManifestData?,
        languageDataCallback: LanguageDataCallback?
    )

    fun getLanguageInfo(sourceLanguage: String): LanguageInfoResponse? {
        var info: LanguageInfoResponse? = null
        executeIO { info = crowdinApi?.getLanguageInfo(sourceLanguage)?.execute()?.body() }
        return info
    }
}
