package land.ver.url.shortener.repositories

import jakarta.transaction.Transactional
import land.ver.url.shortener.repositories.dtos.PagedResult
import land.ver.url.shortener.repositories.dtos.UrlVisitResponse
import java.util.UUID

interface UrlVisitRepository {
    @Transactional
    fun save(urlId: UUID): UrlVisitResponse
    fun getAll(pageNumber: Long): PagedResult<UrlVisitResponse>
}
