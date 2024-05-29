package land.ver.url.shortener.controllers

import jakarta.validation.Valid
import jakarta.validation.constraints.PositiveOrZero
import land.ver.url.shortener.dtos.PagedApiResult
import land.ver.url.shortener.dtos.auditLogs.AuditLogResponseDTO
import land.ver.url.shortener.mappers.AuditLogMapper
import land.ver.url.shortener.repositories.AuditLogsRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/audit-logs")
class AuditLogsController(
    private val auditLogsRepository: AuditLogsRepository,
) {
    @GetMapping("", "/")
    fun index(@Valid @PositiveOrZero @RequestParam pageNumber: Long): PagedApiResult<AuditLogResponseDTO> {
        val logs = auditLogsRepository.getAll(pageNumber)

        return AuditLogMapper().map(logs)
    }
}
