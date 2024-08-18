package land.ver.url.shortener.mappers

import land.ver.url.shortener.SHORT_URL_PATH_PREFIX
import land.ver.url.shortener.dtos.PagedApiResult
import land.ver.url.shortener.dtos.PaginationMetadata
import land.ver.url.shortener.dtos.urls.UrlResponseDTO
import land.ver.url.shortener.models.PagedResult
import land.ver.url.shortener.models.UrlResponse
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class UrlResponseMapper(private val environment: Environment) {
    fun map(pagedResult: PagedResult<UrlResponse>) = PagedApiResult(
        pagedResult.results.map { map(it) },
        PaginationMetadata(
            pageNumber = pagedResult.paginationMetadata.pageNumber,
            pageSize = pagedResult.paginationMetadata.pageSize,
            totalPages = pagedResult.paginationMetadata.totalPages,
        ),
    )

    fun map(url: UrlResponse) = UrlResponseDTO(
        longUrl = url.longUrl,
        shortenedUrl = environment.getProperty("server.base_url") + SHORT_URL_PATH_PREFIX + url.stub,
        createdTimestampUtc = url.createdTimestampUtc.toString(),
        lastVisitTimestampUtc = url.lastVisitedTimestampUtc?.toString(),
    )
}
