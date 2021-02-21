package me.pysquad.cryptobot.subscriber.repo.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import me.pysquad.cryptobot.subscriber.model.ProductIds

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
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