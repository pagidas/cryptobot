package me.pysquad.cryptobot

import me.pysquad.cryptobot.CoinbaseAdapterJson.auto
import me.pysquad.cryptobot.coinbase.GetSandboxCoinbaseProfileMessage
import me.pysquad.cryptobot.coinbase.ProductId
import org.http4k.core.Body
import org.http4k.core.Credentials
import java.time.Instant

data class CoinbaseSubscribeRequest(
        val type: CoinbaseMessageType,
        val productIds: List<ProductId>,
        val channels: List<Channel>
) {
    companion object {
        val lens = Body.auto<CoinbaseSubscribeRequest>().toLens()
    }

    fun toNativeCoinbaseRequest(): String = CoinbaseAdapterJson {
        val productIdsJson = productIds.map { string(it.value) }
        val channelsJson = channels.map { string(it.value) }
        obj(
                "type" to type.name.toLowerCase().asJsonValue(),
                "product_ids" to array(productIdsJson),
                "channels" to array(channelsJson)
        ).toPrettyString()
    }
}

data class AuthenticateRequest(
    val credentials: Credentials
) {
    companion object {
        val lens = Body.auto<AuthenticateRequest>().toLens()
    }
}

data class AuthenticateResponse(
    val authToken: String
) {
    companion object {
        val lens = Body.auto<AuthenticateResponse>().toLens()
    }
}

data class SandboxCoinbaseProfile(
        val id: String,
        val userId: String,
        val name: String,
        val active: Boolean,
        val isDefault: Boolean,
        val createdAt: Instant
) {
    companion object {
        val listLens = Body.auto<List<SandboxCoinbaseProfile>>().toLens()

        fun of(message: GetSandboxCoinbaseProfileMessage): SandboxCoinbaseProfile =
            SandboxCoinbaseProfile(
                    message.id,
                    message.userId,
                    message.name,
                    message.active,
                    message.isDefault,
                    Instant.parse(message.createdAt as CharSequence)
            )
    }
}
