package me.pysquad.cryptobot.security

import me.pysquad.cryptobot.CoinbaseAdapterRepoImpl
import me.pysquad.cryptobot.EnvHelper.Companion.getEnvBasicAuthPassword
import me.pysquad.cryptobot.EnvHelper.Companion.getEnvBasicAuthUsername
import me.pysquad.cryptobot.RealTimeDb
import org.http4k.base64Encode
import org.http4k.contract.security.BasicAuthSecurity
import org.http4k.core.Credentials
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class SecurityProvider(private val coinbaseAdapterRepoImpl: CoinbaseAdapterRepoImpl) {

    // helper static functions

    companion object {

        private const val SHA_256 = "SHA-256"
        private const val HMAC_SHA256 = "HmacSHA256"
        private const val HEX_CHARS = "0123456789ABCDEF"

        fun buildIt() = SecurityProvider(CoinbaseAdapterRepoImpl(RealTimeDb))

        fun hmacSHA256(message: ByteArray, secret: ByteArray): ByteArray {
            val hmacSHA256 = Mac.getInstance(HMAC_SHA256)
            val secretKey = SecretKeySpec(secret, HMAC_SHA256)
            hmacSHA256.init(secretKey)
            return hmacSHA256.doFinal(message)
        }

        fun String.toSHA256(): String {
            val bytes = MessageDigest
                    .getInstance(SHA_256)
                    .digest(toByteArray())
            val result = StringBuilder(bytes.size * 2)

            bytes.forEach {
                val i = it.toInt()
                result.append(HEX_CHARS[i shr 4 and 0x0f])
                result.append(HEX_CHARS[i and 0x0f])
            }

            return result.toString()
        }

        fun basicAuth() =
            BasicAuthSecurity("CoinbaseAdapter", Credentials(getEnvBasicAuthUsername(), getEnvBasicAuthPassword()))
    }

    // operations

    fun getCoinbaseSandboxApiCredentials() = coinbaseAdapterRepoImpl.getCoinbaseSandboxApiCredentials()

    fun getAuthToken(given: Credentials): String? {
        val found = coinbaseAdapterRepoImpl.getCoinbaseAdapterAuthCredentials()

        return if (given.user.toSHA256() == found.user && given.password.toSHA256() == found.password)
            "${given.user}:${given.password}".base64Encode()
        else
            null
    }
}
