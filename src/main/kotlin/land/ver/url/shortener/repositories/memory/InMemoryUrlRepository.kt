package land.ver.url.shortener.repositories.memory

import com.github.f4b6a3.uuid.UuidCreator
import land.ver.url.shortener.repositories.UrlRepository
import land.ver.url.shortener.repositories.dtos.NewUrl
import land.ver.url.shortener.repositories.dtos.PagedResult
import land.ver.url.shortener.repositories.dtos.PaginationMetadata
import land.ver.url.shortener.repositories.dtos.UrlResponse
import land.ver.url.shortener.repositories.memory.models.Url
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository
import java.time.Clock
import java.time.Instant
import kotlin.math.ceil

@Primary
@Repository
class InMemoryUrlRepository(
    private val urls: MutableList<Url>,
    @Value("\${pageSize}") private val pageSize: Int,
) : UrlRepository {

    override fun save(newUrl: NewUrl): UrlResponse {
        val result = UrlResponse(
            id = UuidCreator.getTimeOrderedEpoch(),
            longUrl = newUrl.longUrl,
            stub = newUrl.stub,
            createdTimestampUtc = Instant.now(Clock.systemUTC()),
            lastVisitedTimestampUtc = null,
        )

        urls.add(
            Url(
                id = result.id,
                longUrl = result.longUrl,
                stub = result.stub,
                createdTimestampUtc = result.createdTimestampUtc,
            )
        )

        return result
    }

    override fun getAll(pageNumber: Long): PagedResult<UrlResponse> {
        val count = urls.count()
        val fromIndex = pageNumber.toInt() * pageSize
        val results = if (fromIndex >= count) {
            emptyList()
        } else {
            val toIndex = minOf(fromIndex + pageSize, count)

            urls
                .subList(fromIndex, toIndex)
                .map {
                    UrlResponse(
                        id = it.id,
                        longUrl = it.longUrl,
                        stub = it.stub,
                        createdTimestampUtc = it.createdTimestampUtc,
                        lastVisitedTimestampUtc = null,
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

    override fun findByStub(stub: String): UrlResponse? {
        return urls
            .firstOrNull {
                it.stub == stub
            }
            ?.let {
                UrlResponse(
                    id = it.id,
                    longUrl = it.longUrl,
                    stub = it.stub,
                    createdTimestampUtc = it.createdTimestampUtc,
                    lastVisitedTimestampUtc = null,
                )
            }
    }
}