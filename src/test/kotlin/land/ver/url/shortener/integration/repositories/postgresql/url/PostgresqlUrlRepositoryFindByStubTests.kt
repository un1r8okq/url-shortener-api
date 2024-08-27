package land.ver.url.shortener.integration.repositories.postgresql.url

import jakarta.persistence.EntityManager
import land.ver.url.shortener.integration.repositories.postgresql.BaseRepositoryTest
import land.ver.url.shortener.repositories.postgresql.PostgresqlUrlRepository
import land.ver.url.shortener.repositories.postgresql.models.Url
import land.ver.url.shortener.repositories.postgresql.models.UrlVisit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

class PostgresqlUrlRepositoryFindByStubTests(
    @Autowired private val repository: PostgresqlUrlRepository,
    @Autowired private val entityManager: EntityManager,
) : BaseRepositoryTest() {
    @Test
    fun `when there is no match, the result is null`() {
        val result = repository.findByStub("stub")

        assertNull(result)
    }

    @Test
    fun `when there is a match and the URL hasn't been visited, the response is as expected`() {
        val url = Url(
            id = UUID.fromString("0191905c-b202-778c-8136-40d989ef86d3"),
            longUrl = "https://example.com/a/b/c",
            stub = "stub",
            createdTimestampUtc = LocalDateTime.of(2024, 8, 27, 8, 23).toInstant(ZoneOffset.UTC),
        )
        entityManager.persist(url)
        entityManager.flush()

        val result = repository.findByStub("stub")

        assertNotNull(result)
        assertEquals(url.id, result?.id)
        assertEquals(url.longUrl, result?.longUrl)
        assertEquals(url.stub, result?.stub)
        assertEquals(url.createdTimestampUtc, result?.createdTimestampUtc)
        assertNull(result?.lastVisitedTimestampUtc)
    }

    @Test
    fun `when there is a match and the URL has been visited, the response is as expected`() {
        val url = Url(
            id = UUID.fromString("0191905c-b202-778c-8136-40d989ef86d3"),
            longUrl = "https://example.com/a/b/c",
            stub = "stub",
            createdTimestampUtc = LocalDateTime.of(2024, 8, 27, 8, 23).toInstant(ZoneOffset.UTC),
        )
        val urlVisit = UrlVisit(
            id = UUID.fromString("01919060-5086-7c36-a0a9-10728818a314"),
            urlId = url.id,
            timestampUtc = LocalDateTime.of(2024, 8, 27, 8, 29).toInstant(ZoneOffset.UTC),
        )
        entityManager.persist(url)
        entityManager.persist(urlVisit)
        entityManager.flush()

        val result = repository.findByStub("stub")

        assertNotNull(result)
        assertEquals(url.id, result?.id)
        assertEquals(url.longUrl, result?.longUrl)
        assertEquals(url.stub, result?.stub)
        assertEquals(url.createdTimestampUtc, result?.createdTimestampUtc)
        assertEquals(urlVisit.timestampUtc, result?.lastVisitedTimestampUtc)
    }

    @Test
    fun `when there is a match and a different URL has been visited, the response is as expected`() {
        val urlA = Url(
            id = UUID.fromString("0191905c-b202-778c-8136-40d989ef86d3"),
            longUrl = "https://example.com/a/b/c",
            stub = "abcd",
            createdTimestampUtc = LocalDateTime.of(2024, 8, 27, 8, 23).toInstant(ZoneOffset.UTC),
        )
        val urlB = Url(
            id = UUID.fromString("01919561-4f87-7b96-adaf-c08e3913b56d"),
            longUrl = "https://example.com/d/e/f",
            stub = "efgh",
            createdTimestampUtc = LocalDateTime.of(2024, 8, 28, 7, 47).toInstant(ZoneOffset.UTC),
        )
        val urlBVisit = UrlVisit(
            id = UUID.fromString("01919562-c92e-7b86-bdc9-dd389eff106a"),
            urlId = urlB.id,
            timestampUtc = LocalDateTime.of(2024, 8, 28, 7, 49).toInstant(ZoneOffset.UTC),
        )
        entityManager.persist(urlA)
        entityManager.persist(urlB)
        entityManager.persist(urlBVisit)
        entityManager.flush()

        val result = repository.findByStub(urlA.stub)

        assertNotNull(result)
        assertEquals(urlA.id, result?.id)
        assertEquals(urlA.longUrl, result?.longUrl)
        assertEquals(urlA.stub, result?.stub)
        assertEquals(urlA.createdTimestampUtc, result?.createdTimestampUtc)
        assertNull(result?.lastVisitedTimestampUtc)
    }
}
