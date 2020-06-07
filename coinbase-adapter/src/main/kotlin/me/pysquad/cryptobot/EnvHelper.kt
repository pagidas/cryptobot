package me.pysquad.cryptobot

import me.pysquad.cryptobot.common.MissingEnvVariableException

class EnvHelper {

    companion object {
        @Throws(MissingEnvVariableException::class)
        fun getEnvBasicAuthUsername(): String =
                System.getenv("COINBASE_ADAPTER_BASIC_AUTH_USER")
                        .also {
                            if (it.isNullOrBlank())
                                throw MissingEnvVariableException("coinbase-adapter basic auth username not found from environment")
                        }

        @Throws(MissingEnvVariableException::class)
        fun getEnvBasicAuthPassword(): String =
                System.getenv("COINBASE_ADAPTER_BASIC_AUTH_PWD")
                        .also {
                            if (it.isNullOrBlank())
                                throw MissingEnvVariableException("coinbase-adapter basic auth password not found from environment")
                        }

        @Throws(MissingEnvVariableException::class)
        fun getEnvCoinbaseSandboxApiKey(): String =
                System.getenv("CB_SANDBOX_API_KEY")
                        .also {
                            if (it.isNullOrBlank())
                                throw MissingEnvVariableException("CB_SANDBOX_API_SECRET env variable not found.")
                        }

        @Throws(MissingEnvVariableException::class)
        fun getEnvCoinbaseSandboxApiSecret(): String =
                System.getenv("CB_SANDBOX_API_SECRET")
                        .also {
                            if (it.isNullOrBlank())
                                throw MissingEnvVariableException("CB_SANDBOX_API_SECRET env variable not found.")
                        }

        @Throws(MissingEnvVariableException::class)
        fun getEnvCoinbaseSandboxApiPassphrase(): String =
                System.getenv("CB_SANDBOX_API_PASSPHRASE")
                        .also {
                            if (it.isNullOrBlank())
                                throw MissingEnvVariableException("CB_SANDBOX_API_PASSPHRASE env variable not found.")
                        }
    }
}