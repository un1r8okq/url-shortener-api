package land.ver.url.shortener.repositories.memory.models

import jakarta.persistence.Id
import land.ver.url.shortener.LogType
import java.time.Instant
import java.util.UUID

data class AuditLog(
    @Id
    val id: UUID,
    val createdTimestampUtc: Instant,
    val logType: LogType,
    val message: String,
)
