package land.ver.url.shortener.repositories

import jakarta.transaction.Transactional
import land.ver.url.shortener.repositories.dtos.NewUrl
import land.ver.url.shortener.repositories.dtos.PagedResult
import land.ver.url.shortener.repositories.dtos.UrlResponse

interface UrlRepository {
    @Transactional
    fun save(newUrl: NewUrl): UrlResponse
    fun getAll(pageNumber: Long): PagedResult<UrlResponse>
    fun findByStub(stub: String): UrlResponse?
}
