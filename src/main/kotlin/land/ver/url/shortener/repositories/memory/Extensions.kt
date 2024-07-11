package land.ver.url.shortener.repositories.memory

import land.ver.url.shortener.repositories.dtos.PagedResult
import land.ver.url.shortener.repositories.dtos.PaginationMetadata
import kotlin.math.ceil

fun <T, E> List<T>.getAllPaged(pageSize: Int, pageNumber: Int, mapper: (input: T) -> E): PagedResult<E> {
    val count = count()
    val fromIndex = (pageNumber - 1) * pageSize
    val results = if (fromIndex >= count) {
        emptyList()
    } else {
        val toIndex = minOf(fromIndex + pageSize, count)

        subList(fromIndex, toIndex).map { mapper(it) }
    }

    return PagedResult(
        results = results,
        paginationMetadata = PaginationMetadata(
            pageNumber = pageNumber.toLong(),
            pageSize = pageSize.toLong(),
            totalPages = ceil(count.toFloat() / pageSize).toLong(),
        ),
    )
}
