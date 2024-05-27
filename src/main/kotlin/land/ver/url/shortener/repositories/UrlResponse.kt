package land.ver.url.shortener.repositories

import java.time.Instant
import java.util.UUID

data class UrlResponse(
    val id: UUID,
    val longUrl: String,
    val stub: String,
    val createdTimestampUtc: Instant,
    val lastVisitedTimestampUtc: Instant?
)
