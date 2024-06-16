package land.ver.url.shortener.repositories.memory.models

import java.time.Instant
import java.util.UUID

data class Url(
    val id: UUID,
    val longUrl: String,
    val stub: String,
    val createdTimestampUtc: Instant,
)
