package land.ver.url.shortener.repositories

import jakarta.transaction.Transactional
import land.ver.url.shortener.repositories.dtos.NewUrlVisit
import land.ver.url.shortener.repositories.dtos.PagedResult
import land.ver.url.shortener.repositories.dtos.UrlVisitResponse

interface UrlVisitRepository {
    @Transactional
    fun save(newVisit: NewUrlVisit): UrlVisitResponse
    fun getAll(pageNumber: Long): PagedResult<UrlVisitResponse>
}
