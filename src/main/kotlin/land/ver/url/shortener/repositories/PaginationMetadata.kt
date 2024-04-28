package land.ver.url.shortener.repositories

data class PaginationMetadata(
    val pageNumber: Long,
    val totalPages: Long,
    val pageSize: Long,
)
