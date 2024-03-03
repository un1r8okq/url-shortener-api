package land.ver.url_shortener.dtos

import org.springframework.data.domain.Page

data class PagedApiResult<T>(val data: List<T>, val paginationMetadata: PaginationMetadata?) {
    constructor(pageResult: Page<T>) : this(
        data = pageResult.toList(),
        paginationMetadata = PaginationMetadata(
            pageNumber = pageResult.number,
            totalPages = pageResult.totalPages,
            pageSize = pageResult.size
        ),
    )
}