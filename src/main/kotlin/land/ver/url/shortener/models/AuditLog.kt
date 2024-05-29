package land.ver.url.shortener.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import land.ver.url.shortener.LogType
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "audit_logs")
data class AuditLog(
    @Id
    val id: UUID,
    val createdTimestampUtc: Instant,
    val logType: LogType,
    val message: String,
)
