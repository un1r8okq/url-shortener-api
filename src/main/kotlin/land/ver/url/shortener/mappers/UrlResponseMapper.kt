package land.ver.url.shortener.mappers

import land.ver.url.shortener.SHORT_URL_PATH_PREFIX
import land.ver.url.shortener.dtos.PagedApiResult
import land.ver.url.shortener.dtos.PaginationMetadata
import land.ver.url.shortener.dtos.urls.UrlResponse
import land.ver.url.shortener.models.Url
import land.ver.url.shortener.repositories.PagedResult

class UrlResponseMapper {
    fun map(pagedResult: PagedResult<Url>) = PagedApiResult(
        pagedResult.results.map { map(it) },
        PaginationMetadata(
            pageNumber = pagedResult.paginationMetadata.pageNumber,
            pageSize = pagedResult.paginationMetadata.pageSize,
            totalPages = pagedResult.paginationMetadata.totalPages,
        ),
    )

    fun map(url: Url) = UrlResponse(
        longUrl = url.longUrl,
        shortenedUrl = System.getenv("SERVER_BASE_URL") + SHORT_URL_PATH_PREFIX + url.stub,
        createdTimestampUtc = url.createdTimestampUtc.toString(),
        lastVisitTimestampUtc = null,
    )
}
