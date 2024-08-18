package land.ver.url.shortener.models

import land.ver.url.shortener.LogType
import java.time.Instant
import java.util.UUID

data class AuditLogResponse(
    val id: UUID,
    val createdTimestampUtc: Instant,
    val logType: LogType,
    val message: String,
)
