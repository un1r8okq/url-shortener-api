package land.ver.url.shortener.integration.repositories.postgresql.url.search

import com.github.f4b6a3.uuid.UuidCreator
import jakarta.persistence.EntityManager
import land.ver.url.shortener.integration.repositories.postgresql.BaseRepositoryTest
import land.ver.url.shortener.models.UrlResponse
import land.ver.url.shortener.repositories.postgresql.PostgresqlUrlRepository
import land.ver.url.shortener.repositories.postgresql.models.Url
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant

class PostgresqlUrlRepositoryStubSearchTests(
    @Autowired private val repository: PostgresqlUrlRepository,
    @Autowired private val entityManager: EntityManager,
) : BaseRepositoryTest() {

    @ParameterizedTest
    @ValueSource(strings = ["foob", "1234", "keba", "snak"])
    fun `when there is one URL with a stub that matches the query, it is returned`(stub: String) {
        val url = storeUrl("https://example.com", stub)

        val result = repository.search(stub, 1)

        assertEquals(1, result.results.count())
        assertUrlsEqual(url, result.results[0])
    }

    @Test
    fun `when the stub is lowercase and the query is uppercase it still matches()`() {
        val url = storeUrl("https://example.com", "xkcd")

        val result = repository.search("XKCD", 1)

        assertEquals(1, result.results.count())
        assertUrlsEqual(url, result.results[0])
    }

    @Test
    fun `when the stub is mixed case and the query is uppercase it still matches()`() {
        val url = storeUrl("https://example.com", "xKcD")

        val result = repository.search("XKCD", 1)

        assertEquals(1, result.results.count())
        assertUrlsEqual(url, result.results[0])
    }

    @Test
    fun `percent chars are escaped to match literally`() {
        val expected = storeUrl("https://some-two-strings.com/one", "ab%d")
        storeUrl("https://some-two-strings.com/two", "abcd")

        val result = repository.search("b%d", 1)

        assertEquals(1, result.results.count())
        assertUrlsEqual(expected, result.results.first())
    }

    @Test
    fun `when there are 21 URLs that match there are 3 pages`() {
        (1..21).forEach { i ->
            storeUrl("https://foobar.com/$i", "ab$i")
        }

        val result = repository.search("ab", 1)

        assertEquals(3, result.paginationMetadata.totalPages)
    }

    @Test
    fun `when the search is for 'abcd' and the two stubs are 'abce' and 'abcd' there is one result`() {
        storeUrl("http://example.com", "abce")
        val abcd = storeUrl("https://example.com", "abcd")

        val result = repository.search("abcd", 1)

        assertEquals(1, result.results.count())
        assertUrlsEqual(abcd, result.results.first())
    }

    private fun storeUrl(longUrl: String, stub: String): Url {
        val url = Url(
            id = UuidCreator.getTimeOrderedEpoch(),
            longUrl = longUrl,
            stub = stub,
            createdTimestampUtc = Instant.EPOCH,
        )

        entityManager.persist(url)
        entityManager.flush()

        return url
    }

    private fun assertUrlsEqual(url: Url, urlResponse: UrlResponse) {
        assertEquals(url.id, urlResponse.id)
        assertEquals(url.longUrl, urlResponse.longUrl)
        assertEquals(url.stub, urlResponse.stub)
        assertEquals(url.createdTimestampUtc, urlResponse.createdTimestampUtc)
        assertNull(urlResponse.lastVisitedTimestampUtc)
    }
}
