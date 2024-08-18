package land.ver.url.shortener.models

import java.time.Instant
import java.util.UUID

data class UrlVisitResponse(
    val id: UUID,
    val urlId: UUID,
    val timestampUtc: Instant,
)
