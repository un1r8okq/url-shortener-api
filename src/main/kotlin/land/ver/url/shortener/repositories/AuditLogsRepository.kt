package land.ver.url.shortener.repositories

import jakarta.transaction.Transactional
import land.ver.url.shortener.repositories.dtos.AuditLogResponse
import land.ver.url.shortener.repositories.dtos.NewAuditLog
import land.ver.url.shortener.repositories.dtos.PagedResult

interface AuditLogsRepository {
    @Transactional
    fun save(newAuditLog: NewAuditLog): AuditLogResponse
    fun getAll(pageNumber: Long): PagedResult<AuditLogResponse>
}
