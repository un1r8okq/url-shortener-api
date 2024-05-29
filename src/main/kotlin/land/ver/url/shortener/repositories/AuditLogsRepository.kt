package land.ver.url.shortener.repositories

import com.github.f4b6a3.uuid.UuidCreator
import com.querydsl.jpa.JPQLTemplates
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import land.ver.url.shortener.models.AuditLog
import land.ver.url.shortener.models.QAuditLog
import land.ver.url.shortener.repositories.dtos.NewAuditLog
import land.ver.url.shortener.repositories.dtos.PagedResult
import land.ver.url.shortener.repositories.dtos.PaginationMetadata
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.time.Instant
import kotlin.math.ceil

@Repository
class AuditLogsRepository(
    @PersistenceContext private val entityManager: EntityManager,
    @Value("\${pageSize}") private val pageSize: Long,
) {
    @Transactional
    fun save(newAuditLog: NewAuditLog): AuditLog {
        val queryFactory = JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager)

        val result = AuditLog(
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

    fun getAll(pageNumber: Long): PagedResult<AuditLog> {
        val queryFactory = JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager)

        val count = queryFactory
            .select(QAuditLog.auditLog.count())
            .from(QAuditLog.auditLog)
            .fetchOne()

        val results = queryFactory
            .selectFrom(QAuditLog.auditLog)
            .orderBy(QAuditLog.auditLog.id.asc())
            .offset(pageNumber * pageSize)
            .limit(pageSize)
            .fetch()

        return PagedResult(
            results = results.map {
                AuditLog(
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
