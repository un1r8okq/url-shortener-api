package land.ver.url.shortener.mappers

import land.ver.url.shortener.dtos.PagedApiResult
import land.ver.url.shortener.dtos.PaginationMetadata
import land.ver.url.shortener.dtos.auditLogs.AuditLogResponseDTO
import land.ver.url.shortener.models.AuditLog
import land.ver.url.shortener.repositories.dtos.PagedResult

class AuditLogMapper {
    fun map(pagedResult: PagedResult<AuditLog>) = PagedApiResult(
        pagedResult.results.map { map(it) },
        PaginationMetadata(
            pageNumber = pagedResult.paginationMetadata.pageNumber,
            pageSize = pagedResult.paginationMetadata.pageSize,
            totalPages = pagedResult.paginationMetadata.totalPages,
        ),
    )

    fun map(log: AuditLog) = AuditLogResponseDTO(
        id = log.id,
        createdTimestampUtc = log.createdTimestampUtc.toString(),
        logType = log.logType.strVal,
        message = log.message,
    )
}
