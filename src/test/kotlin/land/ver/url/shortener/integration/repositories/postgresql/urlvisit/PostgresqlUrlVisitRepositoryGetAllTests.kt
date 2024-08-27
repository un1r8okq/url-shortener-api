package land.ver.url.shortener.integration.repositories.postgresql.urlvisit

import com.github.f4b6a3.uuid.UuidCreator
import jakarta.persistence.EntityManager
import land.ver.url.shortener.integration.repositories.postgresql.BaseRepositoryTest
import land.ver.url.shortener.models.UrlVisitResponse
import land.ver.url.shortener.repositories.postgresql.PostgresqlUrlVisitRepository
import land.ver.url.shortener.repositories.postgresql.models.Url
import land.ver.url.shortener.repositories.postgresql.models.UrlVisit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.InvalidDataAccessApiUsageException
import java.time.Instant
import java.util.UUID

class PostgresqlUrlVisitRepositoryGetAllTests(
    @Autowired private val repository: PostgresqlUrlVisitRepository,
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
    fun `when there is one visit, the results list is as expected`() {
        val url = generateUrl()
        val visits = generateVisits(1, url.id)
        entityManager.persist(url)
        visits.forEach { entityManager.persist(it) }
        entityManager.flush()

        val result = repository.getAll(1)

        assertEquals(1, result.results.count())
        assertsVisitsEqual(visits[0], result.results[0])
    }

    @Test
    fun `when there are 21 visits, there are 3 pages`() {
        val url = generateUrl()
        val visits = generateVisits(21, url.id)
        entityManager.persist(url)
        visits.forEach { entityManager.persist(it) }
        entityManager.flush()

        val result = repository.getAll(1)

        assertEquals(3, result.paginationMetadata.totalPages)
    }

    @ParameterizedTest
    @ValueSource(longs = [Long.MIN_VALUE, 0])
    fun `when page number is less than 1, exception is thrown`(pageNumber: Long) {
        assertThrows<InvalidDataAccessApiUsageException> { repository.getAll(pageNumber) }
    }

    private fun generateUrl() =
        Url(
            id = UuidCreator.getTimeOrderedEpoch(),
            longUrl = "https://example.com/?q=something",
            stub = "stub",
            createdTimestampUtc = Instant.EPOCH,
        )

    private fun generateVisits(count: Int, urlId: UUID) = (1..count).map {
        UrlVisit(
            id = UuidCreator.getTimeOrderedEpoch(),
            urlId = urlId,
            timestampUtc = Instant.EPOCH,
        )
    }

    private fun assertsVisitsEqual(urlVisit: UrlVisit, urlVisitResponse: UrlVisitResponse) {
        assertEquals(urlVisit.id, urlVisitResponse.id)
        assertEquals(urlVisit.urlId, urlVisitResponse.urlId)
        assertEquals(urlVisit.timestampUtc, urlVisitResponse.timestampUtc)
    }
}
