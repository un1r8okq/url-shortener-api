package land.ver.url.shortener.dtos.auditLogs

import java.util.UUID

data class AuditLogResponseDTO(
    val id: UUID,
    val createdTimestampUtc: String,
    val logType: String,
    val message: String,
)
