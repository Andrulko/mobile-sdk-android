package com.crowdin.platform

/**
 * Contains configuration properties for initializing Crowdin.
 */
class CrowdinConfig private constructor() {

    // TODO: provide list of file path
    var isPersist: Boolean = false
    var distributionKey: String? = null

    class Builder {
        private var persist: Boolean = false
        private var distributionKey: String? = null

        fun persist(persist: Boolean): Builder {
            this.persist = persist
            return this
        }

        fun withDistributionKey(distributionKey: String): Builder {
            this.distributionKey = distributionKey
            return this
        }

        fun build(): CrowdinConfig {
            val config = CrowdinConfig()
            config.isPersist = persist
            config.distributionKey = distributionKey
            return config
        }
    }
}