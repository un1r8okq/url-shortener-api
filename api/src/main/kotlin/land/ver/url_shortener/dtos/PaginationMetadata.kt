package land.ver.url_shortener.dtos

data class PaginationMetadata(
    val pageNumber: Int,
    val totalPages: Int,
    val pageSize: Int,
)