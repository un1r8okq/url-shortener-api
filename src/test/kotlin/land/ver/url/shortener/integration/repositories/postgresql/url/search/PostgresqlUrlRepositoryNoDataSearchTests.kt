package land.ver.url.shortener.integration.repositories.postgresql.url.search

import land.ver.url.shortener.integration.repositories.postgresql.BaseRepositoryTest
import land.ver.url.shortener.repositories.postgresql.PostgresqlUrlRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.InvalidDataAccessApiUsageException

class PostgresqlUrlRepositoryNoDataSearchTests(
    @Autowired private val repository: PostgresqlUrlRepository,
) : BaseRepositoryTest() {
    private val pageSize = 10L

    @ParameterizedTest
    @ValueSource(strings = ["", "foobar"])
    fun `when there is no data, the results list is empty`(query: String) {
        val result = repository.search(query, 1)

        assertEquals(0, result.results.count())
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "foobar"])
    fun `when there is no data, the page size matches`(query: String) {
        val result = repository.search(query, 1)

        assertEquals(pageSize, result.paginationMetadata.pageSize)
    }

    @ParameterizedTest
    @ValueSource(longs = [1L, 2L, 1_000_000_000L])
    fun `when there is no data, the page number matches`(pageNumber: Long) {
        val result = repository.search("", pageNumber)

        assertEquals(pageNumber, result.paginationMetadata.pageNumber)
    }

    @Test
    fun `when there is no data, the total number of pages matches`() {
        val result = repository.search("", 1)

        assertEquals(0, result.paginationMetadata.totalPages)
    }

    @ParameterizedTest
    @ValueSource(longs = [Long.MIN_VALUE, 0])
    fun `when page number is less than 1, exception is thrown`(pageNumber: Long) {
        assertThrows<InvalidDataAccessApiUsageException> { repository.search("", pageNumber) }
    }
}
