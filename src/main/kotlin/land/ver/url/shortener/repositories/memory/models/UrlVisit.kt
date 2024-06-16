package land.ver.url.shortener.repositories.memory.models

import java.time.Instant
import java.util.UUID

data class UrlVisit(
    val id: UUID,
    val urlId: UUID,
    val timestampUtc: Instant,
)
