package land.ver.url.shortener.repositories.dtos

import java.time.Instant
import java.util.UUID

data class UrlVisitResponse(
    val id: UUID,
    val urlId: UUID,
    val timestampUtc: Instant,
)
