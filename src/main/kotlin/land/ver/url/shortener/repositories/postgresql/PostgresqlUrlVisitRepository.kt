package land.ver.url.shortener.repositories.postgresql

import com.github.f4b6a3.uuid.UuidCreator
import com.querydsl.jpa.JPQLTemplates
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import land.ver.url.shortener.repositories.UrlVisitRepository
import land.ver.url.shortener.repositories.dtos.PagedResult
import land.ver.url.shortener.repositories.dtos.PaginationMetadata
import land.ver.url.shortener.repositories.dtos.UrlVisitResponse
import land.ver.url.shortener.repositories.postgresql.models.QUrlVisit
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID
import kotlin.math.ceil

@Repository
@Profile("postgresql")
class PostgresqlUrlVisitRepository(
    @PersistenceContext private val entityManager: EntityManager,
    @Value("\${pageSize}") private val pageSize: Long,
) : UrlVisitRepository {
    @Transactional
    override fun save(urlId: UUID): UrlVisitResponse {
        val queryFactory = JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager)

        val result = UrlVisitResponse(
            id = UuidCreator.getTimeOrderedEpoch(),
            timestampUtc = Instant.now(),
            urlId = urlId,
        )

        queryFactory
            .insert(QUrlVisit.urlVisit)
            .columns(
                QUrlVisit.urlVisit.id,
                QUrlVisit.urlVisit.urlId,
                QUrlVisit.urlVisit.timestampUtc,
            )
            .values(
                result.id,
                result.urlId,
                result.timestampUtc,
            )
            .execute()

        return result
    }

    override fun getAll(pageNumber: Long): PagedResult<UrlVisitResponse> {
        val queryFactory = JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager)

        val count = queryFactory
            .select(QUrlVisit.urlVisit.count())
            .from(QUrlVisit.urlVisit)
            .fetchOne()

        val results = queryFactory
            .selectFrom(QUrlVisit.urlVisit)
            .orderBy(QUrlVisit.urlVisit.id.asc())
            .offset(pageNumber * pageSize)
            .limit(pageSize)
            .fetch()
            .map {
                UrlVisitResponse(
                    id = it.id,
                    urlId = it.urlId,
                    timestampUtc = it.timestampUtc,
                )
            }

        return PagedResult(
            results = results,
            paginationMetadata = PaginationMetadata(
                pageNumber = pageNumber,
                pageSize = pageSize,
                totalPages = ceil(count!!.toFloat() / pageSize).toLong(),
            ),
        )
    }
}
