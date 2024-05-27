package land.ver.url.shortener.dtos

data class PagedApiResult<T>(val data: List<T>, val paginationMetadata: PaginationMetadata?)
