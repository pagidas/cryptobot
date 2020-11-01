package me.pysquad.cryptobot.client

import org.apache.commons.codec.binary.Base64
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.slf4j.LoggerFactory
import java.time.Instant
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object CoinbaseAuthFilter {
    private val logger = LoggerFactory.getLogger(CoinbaseAuthFilter::class.java)
    private const val HMAC_SHA256 = "HmacSHA256"

    operator fun invoke(coinbaseConfig: CoinbaseConfiguration) = Filter {
        next: HttpHandler -> {
            request: Request ->
                with(coinbaseConfig.authentication) {
                    logger.info("Authenticating incoming request with coinbase: {}", request)
                    val signed = request.signIt(key, secret, passphrase)
                    logger.debug("Produced headers through auth filter {}", signed.headers)
                    next(signed)
                }
        }
    }

    private fun Request.signIt(key: String, secret: String, passphrase: String): Request {
        val timestamp = String.format("%.3f", Instant.now().toEpochMilli() / 1000.0)

        // build the message
        val message = (timestamp + method.name + uri.path + bodyString()).toByteArray()

        // base64 decode the secret
        val decodedSecret = Base64.decodeBase64(secret)

        // sing the message and the decoded secret using the hmac key
        val signature = hmacSHA256(message, decodedSecret)

        // finally base64 encode the result
        val encodedSignature = String(Base64.encodeBase64(signature))

        return headers(listOf(
                "CB-ACCESS-KEY" to key,
                "CB-ACCESS-SIGN" to encodedSignature,
                "CB-ACCESS-TIMESTAMP" to timestamp,
                "CB-ACCESS-PASSPHRASE" to passphrase
        ))
    }

    private fun hmacSHA256(message: ByteArray, secret: ByteArray): ByteArray {
        val hmacSHA256 = Mac.getInstance(HMAC_SHA256)
        val secretKey = SecretKeySpec(secret, HMAC_SHA256)

        hmacSHA256.init(secretKey)
        val signed = hmacSHA256.doFinal(message)

        return signed
    }
}
