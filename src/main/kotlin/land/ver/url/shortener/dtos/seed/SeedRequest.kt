package land.ver.url.shortener.dtos.seed

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class SeedRequest(
    @Min(value = 1)
    @Max(value = 100_000)
    val urlCount: Int,
)
