package land.ver.url.shortener.repositories.postgresql

import com.github.f4b6a3.uuid.UuidCreator
import com.querydsl.jpa.JPQLTemplates
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import land.ver.url.shortener.models.AuditLogResponse
import land.ver.url.shortener.models.NewAuditLog
import land.ver.url.shortener.models.PagedResult
import land.ver.url.shortener.models.PaginationMetadata
import land.ver.url.shortener.repositories.AuditLogsRepository
import land.ver.url.shortener.repositories.postgresql.models.QAuditLog
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository
import java.time.Instant
import kotlin.math.ceil

@Repository
@Primary
class PostgresqlAuditLogsRepository(
    @PersistenceContext private val entityManager: EntityManager,
    @Value("\${pageSize}") private val pageSize: Long,
) : AuditLogsRepository {
    @Transactional
    override fun save(newAuditLog: NewAuditLog): AuditLogResponse {
        val queryFactory = JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager)

        val result = AuditLogResponse(
            id = UuidCreator.getTimeOrderedEpoch(),
            createdTimestampUtc = Instant.now(),
            logType = newAuditLog.logType,
            message = newAuditLog.message,
        )

        queryFactory
            .insert(QAuditLog.auditLog)
            .columns(
                QAuditLog.auditLog.id,
                QAuditLog.auditLog.createdTimestampUtc,
                QAuditLog.auditLog.logType,
                QAuditLog.auditLog.message,
            )
            .values(
                result.id,
                result.createdTimestampUtc,
                result.logType,
                result.message,
            )
            .execute()

        return result
    }

    override fun getAll(pageNumber: Long): PagedResult<AuditLogResponse> {
        require(pageNumber > 0)

        val queryFactory = JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager)

        val count = queryFactory
            .select(QAuditLog.auditLog.count())
            .from(QAuditLog.auditLog)
            .fetchOne()

        val results = queryFactory
            .selectFrom(QAuditLog.auditLog)
            .orderBy(QAuditLog.auditLog.id.desc())
            .offset((pageNumber - 1) * pageSize)
            .limit(pageSize)
            .fetch()

        return PagedResult(
            results = results.map {
                AuditLogResponse(
                    id = it.id,
                    createdTimestampUtc = it.createdTimestampUtc,
                    logType = it.logType,
                    message = it.message,
                )
            },
            paginationMetadata = PaginationMetadata(
                pageNumber = pageNumber,
                pageSize = pageSize,
                totalPages = ceil(count!!.toFloat() / pageSize).toLong(),
            ),
        )
    }
}
