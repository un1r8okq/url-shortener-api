package land.ver.url.shortener.dtos

import land.ver.url.shortener.repositories.dtos.PagedResult

data class PagedApiResult<T>(val data: List<T>, val paginationMetadata: PaginationMetadata?) {
    constructor(pageResult: PagedResult<T>) : this(
        data = pageResult.results,
        paginationMetadata = PaginationMetadata(
            pageNumber = pageResult.paginationMetadata.pageNumber,
            totalPages = pageResult.paginationMetadata.totalPages,
            pageSize = pageResult.paginationMetadata.pageSize,
        ),
    )
}
