package land.ver.url.shortener.unit.url.repository

import com.github.f4b6a3.uuid.UuidCreator
import io.mockk.verify
import land.ver.url.shortener.models.Url
import land.ver.url.shortener.repositories.UrlRepository
import land.ver.url.shortener.unit.helpers.MockEntityManagerBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.Instant

class UrlRepositoryGetAllTests {
    private val pageSize = 10L

    @Test
    fun `when there is no data, the results list is empty`() {
        val entityManager = MockEntityManagerBuilder().build()
        val repository = UrlRepository(entityManager, pageSize)

        val result = repository.getAll(0)

        assertEquals(0, result.results.count())
    }

    @Test
    fun `when there is no data, the page size matches`() {
        val entityManager = MockEntityManagerBuilder().build()
        val repository = UrlRepository(entityManager, pageSize)

        val result = repository.getAll(0)

        assertEquals(pageSize, result.paginationMetadata.pageSize)
    }

    @ParameterizedTest
    @ValueSource(longs = [0L, 1L, 1_000_000_000L])
    fun `when there is no data, the page number matches`(pageNumber: Long) {
        val entityManager = MockEntityManagerBuilder().build()
        val repository = UrlRepository(entityManager, pageSize)

        val result = repository.getAll(pageNumber)

        assertEquals(pageNumber, result.paginationMetadata.pageNumber)
    }

    @Test
    fun `when there is no data, the total number of pages matches`() {
        val entityManager = MockEntityManagerBuilder()
            .returningSingleResult(0)
            .build()
        val repository = UrlRepository(entityManager, pageSize)

        val result = repository.getAll(0)

        assertEquals(0, result.paginationMetadata.totalPages)
    }

    @Test
    fun `when there is one URL, the results list is as expected`() {
        val testUrl = generateUrls(1)[0]
        val entityManager = MockEntityManagerBuilder()
            .returningUrls(listOf(testUrl))
            .build()
        val repository = UrlRepository(entityManager, pageSize)

        val result = repository.getAll(0)

        assertEquals(1, result.results.count())
        assertEquals(testUrl, result.results[0])
    }

    @Test
    fun `when there are 21 URLs, when we get page 0, the entity manager is passed the expected parameters`() {
        val testUrls = generateUrls(21)
        val entityManagerBuilder = MockEntityManagerBuilder()
            .returningUrls(testUrls)
        val entityManager = entityManagerBuilder.build()
        val repository = UrlRepository(entityManager, pageSize)

        repository.getAll(0)

        verify(exactly = 1) { entityManagerBuilder.query.setMaxResults(pageSize.toInt()) }
        verify(exactly = 1) { entityManagerBuilder.query.setFirstResult(0) }
    }

    @Test
    fun `when there are 21 URLs, there are 3 pages`() {
        val testUrls = generateUrls(21)
        val entityManager = MockEntityManagerBuilder()
            .returningSingleResult(21)
            .returningUrls(testUrls)
            .build()
        val repository = UrlRepository(entityManager, pageSize)

        val result = repository.getAll(0)

        assertEquals(3, result.paginationMetadata.totalPages)
    }

    private fun generateUrls(count: Int) = (0..count).map {
        Url(
            id = UuidCreator.getTimeOrderedEpoch(),
            longUrl = "https://example.com/?q=something$it",
            stub = "abc$it",
            createdTimestampUtc = Instant.EPOCH,
        )
    }
}
