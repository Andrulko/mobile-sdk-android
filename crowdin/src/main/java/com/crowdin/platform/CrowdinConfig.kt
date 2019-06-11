package com.crowdin.platform

import com.crowdin.platform.data.remote.NetworkType

/**
 * Contains configuration properties for initializing Crowdin.
 */
class CrowdinConfig private constructor() {

    var isPersist: Boolean = true
    var distributionKey: String? = null
    var filePaths: Array<out String>? = null
    var networkType: NetworkType = NetworkType.ALL
    var isRealTimeUpdateEnabled: Boolean = false
    var isScreenshotEnabled: Boolean = false
    var updateInterval: Long = -1
    var sourceLanguage: String = ""

    class Builder {

        private var isPersist: Boolean = true
        private var distributionKey: String? = null
        private var filePaths: Array<out String>? = null
        private var networkType: NetworkType = NetworkType.ALL
        private var isRealTimeUpdateEnabled: Boolean = false
        private var isScreenshotEnabled: Boolean = false
        private var updateInterval: Long = -1
        private var sourceLanguage: String = ""

        fun persist(isPersist: Boolean): Builder {
            this.isPersist = isPersist
            return this
        }

        fun withDistributionKey(distributionKey: String): Builder {
            this.distributionKey = distributionKey
            return this
        }

        fun withFilePaths(vararg filePaths: String): Builder {
            this.filePaths = filePaths
            return this
        }

        fun withNetworkType(networkType: NetworkType): Builder {
            this.networkType = networkType
            return this
        }

        fun withRealTimeUpdates(isRealTimeUpdateEnabled: Boolean): Builder {
            this.isRealTimeUpdateEnabled = isRealTimeUpdateEnabled
            return this
        }

        fun withScreenshotEnabled(isScreenshotEnabled: Boolean): Builder {
            this.isScreenshotEnabled = isScreenshotEnabled
            return this
        }

        fun withSourceLanguage(sourceLanguage: String): Builder {
            this.sourceLanguage = sourceLanguage
            return this
        }

        fun withUpdateInterval(updateInterval: Long): Builder {
            this.updateInterval = updateInterval
            return this
        }

        fun build(): CrowdinConfig {
            val config = CrowdinConfig()
            config.isPersist = isPersist
            if (distributionKey == null) {
                throw IllegalArgumentException("Crowdin: `distributionKey` cannot be null")
            }
            config.distributionKey = distributionKey

            if (filePaths == null) {
                throw IllegalArgumentException("Crowdin: `filePaths` cannot be null")
            }
            config.filePaths = filePaths
            config.networkType = networkType
            config.isRealTimeUpdateEnabled = isRealTimeUpdateEnabled
            config.isScreenshotEnabled = isScreenshotEnabled

            if ((isRealTimeUpdateEnabled || isScreenshotEnabled) && sourceLanguage.isEmpty()) {
                throw IllegalArgumentException("Crowdin: `sourceLanguage` cannot be empty")
            }
            config.sourceLanguage = sourceLanguage
            config.updateInterval = updateInterval

            return config
        }
    }
}