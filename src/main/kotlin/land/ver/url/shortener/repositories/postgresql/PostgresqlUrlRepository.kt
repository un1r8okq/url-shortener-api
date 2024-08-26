package land.ver.url.shortener.repositories.postgresql

import com.github.f4b6a3.uuid.UuidCreator
import com.querydsl.jpa.JPQLTemplates
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import land.ver.url.shortener.exceptions.InvalidPageNumberException
import land.ver.url.shortener.models.NewUrl
import land.ver.url.shortener.models.PagedResult
import land.ver.url.shortener.models.PaginationMetadata
import land.ver.url.shortener.models.UrlResponse
import land.ver.url.shortener.repositories.UrlRepository
import land.ver.url.shortener.repositories.postgresql.models.QUrl
import land.ver.url.shortener.repositories.postgresql.models.QUrlVisit
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository
import java.time.Clock
import java.time.Instant
import kotlin.math.ceil

@Repository
@Primary
class PostgresqlUrlRepository(
    @PersistenceContext private val entityManager: EntityManager,
    @Value("\${pageSize}") private val pageSize: Long,
) : UrlRepository {
    override fun save(newUrl: NewUrl): UrlResponse {
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

    override fun getAll(pageNumber: Long): PagedResult<UrlResponse> {
        if (pageNumber < 1) {
            throw InvalidPageNumberException(pageNumber)
        }
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
            .orderBy(QUrl.url.id.desc(), QUrlVisit.urlVisit.timestampUtc.max().asc())
            .offset((pageNumber - 1) * pageSize)
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

    override fun findByStub(stub: String): UrlResponse? {
        val queryFactory = JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager)

        return queryFactory
            .select(QUrl.url, QUrlVisit.urlVisit.timestampUtc.max())
            .from(QUrl.url)
            .leftJoin(QUrlVisit.urlVisit).on(QUrl.url.id.eq(QUrlVisit.urlVisit.urlId))
            .groupBy(QUrl.url.id)
            .where(QUrl.url.stub.eq(stub))
            .fetchOne()
            ?.let {
                val url = it.get(QUrl.url)
                val lastVisited = it.get(1, Instant::class.java)

                UrlResponse(
                    id = url!!.id,
                    longUrl = url.longUrl,
                    stub = url.stub,
                    createdTimestampUtc = url.createdTimestampUtc,
                    lastVisitedTimestampUtc = lastVisited,
                )
            }
    }
}
