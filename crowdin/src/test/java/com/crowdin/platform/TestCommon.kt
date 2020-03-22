package com.crowdin.platform

import com.crowdin.platform.data.model.ManifestData
import com.crowdin.platform.data.remote.api.CrowdinDistributionApi
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal fun givenManifestData(): ManifestData =
    Gson().fromJson(
        "{\"files\":[\"\\/strings.xml\"]}",
        ManifestData::class.java
    )

internal fun givenMockMappingFileResponse(
    mockDistributionApi: CrowdinDistributionApi,
    success: Boolean = true,
    successCode: Int = 200
) {
    val mockedCall = mock(Call::class.java) as Call<ResponseBody>
    `when`(mockDistributionApi.getMappingFile(any(), any(), any(), any())).thenReturn(mockedCall)

    val response = if (success) {
        Response.success<ResponseBody>(successCode, StubResponseBody())
    } else {
        Response.error(403, StubResponseBody())
    }
    `when`(mockedCall.execute()).thenReturn(response)
}

internal fun givenMockManifestResponse(
    mockDistributionApi: CrowdinDistributionApi,
    success: Boolean = true,
    successCode: Int = 200
) {
    val mockedCall = mock(Call::class.java) as Call<ResponseBody>
    `when`(mockDistributionApi.getResourceManifest(any(), any())).thenReturn(mockedCall)
    val responseBody = mock(StubResponseBody::class.java)
    val json = "{\"files\":[\"\\/strings.xml\"]}"
    `when`(responseBody.string()).thenReturn(json)

    val response = if (success) {
        Response.success<ResponseBody>(successCode, responseBody)
    } else {
        Response.error(403, StubResponseBody())
    }

    doAnswer {
        val callback = it.getArgument(0, Callback::class.java) as Callback<ResponseBody>
        callback.onResponse(mockedCall, response)
    }.`when`(mockedCall).enqueue(any())
}
