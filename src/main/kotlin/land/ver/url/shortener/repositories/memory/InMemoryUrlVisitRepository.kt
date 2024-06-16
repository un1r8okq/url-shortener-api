package land.ver.url.shortener.repositories.memory

import com.github.f4b6a3.uuid.UuidCreator
import jakarta.transaction.Transactional
import land.ver.url.shortener.repositories.UrlVisitRepository
import land.ver.url.shortener.repositories.dtos.NewUrlVisit
import land.ver.url.shortener.repositories.dtos.PagedResult
import land.ver.url.shortener.repositories.dtos.UrlVisitResponse
import land.ver.url.shortener.repositories.memory.models.UrlVisit
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository
import java.time.Instant

@Primary
@Repository
class InMemoryUrlVisitRepository(
    private val urlVisits: MutableList<UrlVisit>,
    @Value("\${pageSize}") private val pageSize: Int,
) : UrlVisitRepository {
    @Transactional
    override fun save(newVisit: NewUrlVisit): UrlVisitResponse {
        val result = UrlVisitResponse(
            id = UuidCreator.getTimeOrderedEpoch(),
            urlId = newVisit.urlId,
            timestampUtc = Instant.now(),
        )

        urlVisits.add(
            UrlVisit(
                id = result.id,
                urlId = result.urlId,
                timestampUtc = result.timestampUtc,
            )
        )

        return result
    }

    override fun getAll(pageNumber: Long): PagedResult<UrlVisitResponse> {
        return urlVisits.getAllPaged(pageSize, pageNumber.toInt()) {
            UrlVisitResponse(
                id = it.id,
                urlId = it.urlId,
                timestampUtc = it.timestampUtc,
            )
        }
    }
}
