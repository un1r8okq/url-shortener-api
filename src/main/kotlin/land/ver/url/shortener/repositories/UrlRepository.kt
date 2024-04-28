package land.ver.url.shortener.repositories

import com.github.f4b6a3.uuid.UuidCreator
import com.querydsl.jpa.JPQLTemplates
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import land.ver.url.shortener.models.QUrl
import land.ver.url.shortener.models.Url
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
    fun save(newUrl: NewUrl): Url {
        val queryFactory = JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager)
        val url = QUrl.url

        val result = Url(
            id = UuidCreator.getTimeOrderedEpoch(),
            longUrl = newUrl.longUrl,
            stub = newUrl.stub,
            createdTimestampUtc = Instant.now(Clock.systemUTC()),
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

    fun getAll(pageNumber: Long): PagedResult<Url> {
        val queryFactory = JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager)

        val urlCount = queryFactory
            .select(QUrl.url.count())
            .from(QUrl.url)
            .fetchOne()

        val urls = queryFactory
            .selectFrom(QUrl.url)
            .offset(pageNumber * pageSize)
            .limit(pageSize)
            .fetch()

        return PagedResult(
            results = urls,
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
