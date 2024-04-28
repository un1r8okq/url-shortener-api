package land.ver.url.shortener.repositories

data class PagedResult<T>(
    val results: List<T>,
    val paginationMetadata: PaginationMetadata,
)
