package land.ver.url_shortener.http

import org.springframework.data.domain.Page

data class PagedApiResult<T>(val data: List<T>, val paginationMetadata: PaginationMetadata?) {
    constructor(pageResult: Page<T>) : this(
        data = pageResult.toList(),
        paginationMetadata = PaginationMetadata(
            pageNumber = pageResult.number + 1,
            totalPages = pageResult.totalPages,
            pageSize = pageResult.size
        ),
    )
}