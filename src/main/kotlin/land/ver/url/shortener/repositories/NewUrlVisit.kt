package land.ver.url.shortener.repositories

import land.ver.url.shortener.models.Url
import java.time.Instant

data class NewUrlVisit(
    val timestampUtc: Instant,
    val url: Url,
)
