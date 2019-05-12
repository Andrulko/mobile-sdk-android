package com.crowdin.platform.data.remote.api

internal data class CreateScreenshotRequestBody(var storageId: Int, var name: String)

internal data class CreateScreenshotResponse(var data: Data)

internal data class TagData(var stringId: Int,
                            var position: Position)

internal data class Position(var x: Int,
                             var y: Int,
                             var width: Int,
                             var height: Int)

internal data class UploadScreenshotResponse(var data: Data? = null)

internal data class Data(var id: Int? = null)