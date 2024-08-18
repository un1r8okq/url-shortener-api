package land.ver.url.shortener.repositories

import jakarta.transaction.Transactional
import land.ver.url.shortener.models.NewUrl
import land.ver.url.shortener.models.PagedResult
import land.ver.url.shortener.models.UrlResponse

interface UrlRepository {
    @Transactional
    fun save(newUrl: NewUrl): UrlResponse
    fun getAll(pageNumber: Long): PagedResult<UrlResponse>
    fun findByStub(stub: String): UrlResponse?
}
