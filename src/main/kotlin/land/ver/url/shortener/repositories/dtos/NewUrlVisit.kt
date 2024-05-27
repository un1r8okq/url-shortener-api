package land.ver.url.shortener.repositories.dtos

import land.ver.url.shortener.models.Url
import java.time.Instant

data class NewUrlVisit(
    val timestampUtc: Instant,
    val url: Url,
)
