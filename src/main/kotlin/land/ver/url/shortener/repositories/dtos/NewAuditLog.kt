package land.ver.url.shortener.repositories.dtos

import land.ver.url.shortener.LogType

data class NewAuditLog(
    val logType: LogType,
    val message: String,
)
