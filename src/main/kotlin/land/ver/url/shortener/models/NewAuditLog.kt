package land.ver.url.shortener.models

import land.ver.url.shortener.LogType

data class NewAuditLog(
    val logType: LogType,
    val message: String,
)
