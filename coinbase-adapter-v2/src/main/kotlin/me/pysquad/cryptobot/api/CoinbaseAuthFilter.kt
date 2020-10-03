package me.pysquad.cryptobot.api

import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.annotation.Filter
import io.micronaut.http.filter.ClientFilterChain
import io.micronaut.http.filter.HttpClientFilter
import org.apache.commons.codec.binary.Base64
import org.reactivestreams.Publisher
import java.time.Instant
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Filter("\${coinbase.url}")
class CoinbaseAuthFilter(private val coinbaseConfiguration: CoinbaseConfiguration) : HttpClientFilter {

    private val HMAC_SHA256 = "HmacSHA256"

    override fun doFilter(request: MutableHttpRequest<*>?, chain: ClientFilterChain?): Publisher<out HttpResponse<*>>? =
            chain?.proceed(request?.signIt(
                    coinbaseConfiguration.authentication.key,
                    coinbaseConfiguration.authentication.secret,
                    coinbaseConfiguration.authentication.passphrase
            ))

    private fun MutableHttpRequest<*>.signIt(key: String, secret: String, passphrase: String): MutableHttpRequest<*> {
        val timestamp = String.format("%.3f", Instant.now().toEpochMilli() / 1000.0)

        // build the message
        val message = (timestamp + method.name + uri.path + getBody(String::class.java)).toByteArray()

        // base64 decode the secret
        val decodedSecret = Base64.decodeBase64(secret)

        // sing the message and the decoded secret using the hmac key
        val signature = hmacSHA256(message, decodedSecret)

        // finally base64 encode the result
        val encodedSignature = String(Base64.encodeBase64(signature))

        return headers {
            it.add("CB-ACCESS-KEY", key)
            it.add("CB-ACCESS-SIGN", encodedSignature)
            it.add("CB-ACCESS-TIMESTAMP", timestamp)
            it.add("CB-ACCESS-PASSPHRASE", passphrase)
        }
    }

    private fun hmacSHA256(message: ByteArray, secret: ByteArray): ByteArray {
        val hmacSHA256 = Mac.getInstance(HMAC_SHA256)
        val secretKey = SecretKeySpec(secret, HMAC_SHA256)

        hmacSHA256.init(secretKey)
        val signed = hmacSHA256.doFinal(message)

        return signed
    }
}