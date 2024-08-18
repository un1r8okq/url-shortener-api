package land.ver.url.shortener.repositories

import jakarta.transaction.Transactional
import land.ver.url.shortener.models.AuditLogResponse
import land.ver.url.shortener.models.NewAuditLog
import land.ver.url.shortener.models.PagedResult

interface AuditLogsRepository {
    @Transactional
    fun save(newAuditLog: NewAuditLog): AuditLogResponse
    fun getAll(pageNumber: Long): PagedResult<AuditLogResponse>
}
