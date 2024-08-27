package land.ver.url.shortener.integration.repositories.postgresql.url

import com.github.f4b6a3.uuid.UuidCreator
import jakarta.persistence.EntityManager
import land.ver.url.shortener.integration.repositories.postgresql.BaseRepositoryTest
import land.ver.url.shortener.models.UrlResponse
import land.ver.url.shortener.repositories.postgresql.PostgresqlUrlRepository
import land.ver.url.shortener.repositories.postgresql.models.Url
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.InvalidDataAccessApiUsageException
import java.time.Instant

class PostgresqlUrlRepositoryGetAllTests(
    @Autowired private val repository: PostgresqlUrlRepository,
    @Autowired private val entityManager: EntityManager,
) : BaseRepositoryTest() {
    private val pageSize = 10L

    @Test
    fun `when there is no data, the results list is empty`() {
        val result = repository.getAll(1)

        assertEquals(0, result.results.count())
    }

    @Test
    fun `when there is no data, the page size matches`() {
        val result = repository.getAll(1)

        assertEquals(pageSize, result.paginationMetadata.pageSize)
    }

    @ParameterizedTest
    @ValueSource(longs = [1L, 2L, 1_000_000_000L])
    fun `when there is no data, the page number matches`(pageNumber: Long) {
        val result = repository.getAll(pageNumber)

        assertEquals(pageNumber, result.paginationMetadata.pageNumber)
    }

    @Test
    fun `when there is no data, the total number of pages matches`() {
        val result = repository.getAll(1)

        assertEquals(0, result.paginationMetadata.totalPages)
    }

    @Test
    fun `when there is one URL, the results list is as expected`() {
        val urls = generateUrls(1)
        urls.forEach { entityManager.persist(it) }
        entityManager.flush()

        val result = repository.getAll(1)

        assertEquals(1, result.results.count())
        assertUrlsEqual(urls[0], result.results[0])
    }

    @Test
    fun `when there are 21 URLs, there are 3 pages`() {
        val urls = generateUrls(21)
        urls.forEach { entityManager.persist(it) }
        entityManager.flush()

        val result = repository.getAll(1)

        assertEquals(3, result.paginationMetadata.totalPages)
    }

    @ParameterizedTest
    @ValueSource(longs = [Long.MIN_VALUE, 0])
    fun `when page number is less than 1, exception is thrown`(pageNumber: Long) {
        assertThrows<InvalidDataAccessApiUsageException> { repository.getAll(pageNumber) }
    }

    private fun generateUrls(count: Int) = (1..count).map {
        Url(
            id = UuidCreator.getTimeOrderedEpoch(),
            longUrl = "https://example.com/?q=something$it",
            stub = it.toString(),
            createdTimestampUtc = Instant.EPOCH,
        )
    }

    private fun assertUrlsEqual(url: Url, urlResponse: UrlResponse) {
        assertEquals(url.id, urlResponse.id)
        assertEquals(url.longUrl, urlResponse.longUrl)
        assertEquals(url.stub, urlResponse.stub)
        assertEquals(url.createdTimestampUtc, urlResponse.createdTimestampUtc)
        assertNull(urlResponse.lastVisitedTimestampUtc)
    }
}
