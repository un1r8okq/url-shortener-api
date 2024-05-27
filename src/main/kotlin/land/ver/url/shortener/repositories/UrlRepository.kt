package land.ver.url.shortener.repositories

import com.github.f4b6a3.uuid.UuidCreator
import com.querydsl.jpa.JPQLTemplates
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import land.ver.url.shortener.models.QUrl
import land.ver.url.shortener.models.QUrlVisit
import land.ver.url.shortener.models.Url
import land.ver.url.shortener.repositories.dtos.NewUrl
import land.ver.url.shortener.repositories.dtos.PagedResult
import land.ver.url.shortener.repositories.dtos.PaginationMetadata
import land.ver.url.shortener.repositories.dtos.UrlResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.time.Clock
import java.time.Instant
import kotlin.math.ceil

@Repository
class UrlRepository(
    @PersistenceContext private val entityManager: EntityManager,
    @Value("\${pageSize}") private val pageSize: Long,
) {
    @Transactional
    fun save(newUrl: NewUrl): UrlResponse {
        val queryFactory = JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager)
        val url = QUrl.url

        val result = UrlResponse(
            id = UuidCreator.getTimeOrderedEpoch(),
            longUrl = newUrl.longUrl,
            stub = newUrl.stub,
            createdTimestampUtc = Instant.now(Clock.systemUTC()),
            lastVisitedTimestampUtc = null,
        )

        queryFactory
            .insert(url)
            .columns(
                url.id,
                url.longUrl,
                url.stub,
                url.createdTimestampUtc,
            )
            .values(
                result.id,
                result.longUrl,
                result.stub,
                result.createdTimestampUtc,
            )
            .execute()

        return result
    }

    fun getAll(pageNumber: Long): PagedResult<UrlResponse> {
        val queryFactory = JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager)

        val urlCount = queryFactory
            .select(QUrl.url.count())
            .from(QUrl.url)
            .fetchOne()

        val results = queryFactory
            .select(QUrl.url, QUrlVisit.urlVisit.timestampUtc.max())
            .from(QUrl.url)
            .leftJoin(QUrlVisit.urlVisit).on(QUrl.url.id.eq(QUrlVisit.urlVisit.urlId))
            .groupBy(QUrl.url.id)
            .orderBy(QUrl.url.id.asc(), QUrlVisit.urlVisit.timestampUtc.max().asc())
            .offset(pageNumber * pageSize)
            .limit(pageSize)
            .fetch()

        return PagedResult(
            results = results.map {
                val url = it.get(QUrl.url)
                val lastVisited = it.get(1, Instant::class.java)

                UrlResponse(
                    id = url!!.id,
                    longUrl = url.longUrl,
                    stub = url.stub,
                    createdTimestampUtc = url.createdTimestampUtc,
                    lastVisitedTimestampUtc = lastVisited,
                )
            },
            paginationMetadata = PaginationMetadata(
                pageNumber = pageNumber,
                pageSize = pageSize,
                totalPages = ceil(urlCount!!.toFloat() / pageSize).toLong(),
            ),
        )
    }

    fun findByStub(stub: String): Url? {
        val queryFactory = JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager)

        return queryFactory
            .selectFrom(QUrl.url)
            .where(QUrl.url.stub.eq(stub))
            .fetchOne()
    }
}
