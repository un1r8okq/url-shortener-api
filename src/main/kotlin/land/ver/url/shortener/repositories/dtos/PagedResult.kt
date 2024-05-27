package land.ver.url.shortener.repositories.dtos

data class PagedResult<T>(
    val results: List<T>,
    val paginationMetadata: PaginationMetadata,
)
