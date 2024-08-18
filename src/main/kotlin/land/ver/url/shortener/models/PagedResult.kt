package land.ver.url.shortener.models

data class PagedResult<T>(
    val results: List<T>,
    val paginationMetadata: PaginationMetadata,
)
