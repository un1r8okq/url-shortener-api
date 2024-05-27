package land.ver.url.shortener.repositories

import com.github.f4b6a3.uuid.UuidCreator
import com.querydsl.jpa.JPQLTemplates
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import land.ver.url.shortener.models.QUrlVisit
import land.ver.url.shortener.models.UrlVisit
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import kotlin.math.ceil

@Repository
class UrlVisitRepository(
    @PersistenceContext private val entityManager: EntityManager,
    @Value("\${pageSize}") private val pageSize: Long,
) {
    @Transactional
    fun save(newVisit: NewUrlVisit): UrlVisit {
        val queryFactory = JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager)

        val result = UrlVisit(
            id = UuidCreator.getTimeOrderedEpoch(),
            timestampUtc = newVisit.timestampUtc,
            urlId = newVisit.url.id,
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

    fun getAll(pageNumber: Long): PagedResult<UrlVisit> {
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
