package me.pysquad.cryptobot.subscriber.repo.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import me.pysquad.cryptobot.subscriber.model.ProductIds

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class ProductSubscriptionDto(
        var subId: String = "",
        var productIds: String = ""
) {
    val productIdsAsList: ProductIds
        @JsonIgnore
        get() =
            if (productIds.isBlank())
                emptyList()
            else {
                productIds.split(",").toMutableList()
                        .apply { removeAll { it.isBlank() } }
                        .toList()
            }
}