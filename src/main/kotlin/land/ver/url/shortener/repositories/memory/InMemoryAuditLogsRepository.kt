package land.ver.url.shortener.repositories.memory

import com.github.f4b6a3.uuid.UuidCreator
import jakarta.transaction.Transactional
import land.ver.url.shortener.repositories.AuditLogsRepository
import land.ver.url.shortener.repositories.dtos.AuditLogResponse
import land.ver.url.shortener.repositories.dtos.NewAuditLog
import land.ver.url.shortener.repositories.dtos.PagedResult
import land.ver.url.shortener.repositories.dtos.PaginationMetadata
import land.ver.url.shortener.repositories.memory.models.AuditLog
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository
import java.time.Instant
import kotlin.math.ceil

@Primary
@Repository
class InMemoryAuditLogsRepository(
    private val logs: MutableList<AuditLog>,
    @Value("\${pageSize}") private val pageSize: Int,
) : AuditLogsRepository {
    @Transactional
    override fun save(newAuditLog: NewAuditLog): AuditLogResponse {
        val result = AuditLogResponse(
            id = UuidCreator.getTimeOrderedEpoch(),
            createdTimestampUtc = Instant.now(),
            logType = newAuditLog.logType,
            message = newAuditLog.message,
        )

        logs.add(AuditLog(
            id = result.id,
            createdTimestampUtc = result.createdTimestampUtc,
            logType = result.logType,
            message = result.message,
        ))

        return result
    }

    override fun getAll(pageNumber: Long): PagedResult<AuditLogResponse> {
        val count = logs.count()
        val fromIndex = pageNumber.toInt() * pageSize
        val results = if (fromIndex >= count) {
            emptyList()
        } else {
            val toIndex = minOf(fromIndex + pageSize, count)

            logs
                .subList(fromIndex, toIndex)
                .map {
                    AuditLogResponse(
                        id = it.id,
                        createdTimestampUtc = it.createdTimestampUtc,
                        logType = it.logType,
                        message = it.message,
                    )
                }
        }

        return PagedResult(
            results = results,
            paginationMetadata = PaginationMetadata(
                pageNumber = pageNumber,
                pageSize = pageSize.toLong(),
                totalPages = ceil(count.toFloat() / pageSize).toLong(),
            ),
        )
    }
}
