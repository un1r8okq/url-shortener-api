package land.ver.url.shortener.dtos

data class PaginationMetadata(
    val pageNumber: Long,
    val totalPages: Long,
    val pageSize: Long,
)
