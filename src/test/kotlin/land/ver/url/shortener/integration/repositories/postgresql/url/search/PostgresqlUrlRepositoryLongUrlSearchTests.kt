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

class PostgresqlUrlRepositoryLongUrlSearchTests(
    @Autowired private val repository: PostgresqlUrlRepository,
    @Autowired private val entityManager: EntityManager,
) : BaseRepositoryTest() {

    @ParameterizedTest
    @ValueSource(strings = ["foobar", "1234", "kebab-case", "snake_case"])
    fun `when there is one URL with a domain that matches the query, it is returned`(domain: String) {
        val url = storeUrl("https://$domain.com", "xkcd")

        val result = repository.search(domain, 1)

        assertEquals(1, result.results.count())
        assertUrlsEqual(url, result.results[0])
    }

    @Test
    fun `when the URL is lowercase and the query is uppercase it still matches()`() {
        val url = storeUrl("https://example.com", "xkcd")

        val result = repository.search("EXAMPLE", 1)

        assertEquals(1, result.results.count())
        assertUrlsEqual(url, result.results[0])
    }

    @Test
    fun `percent chars are escaped to match literally`() {
        val expected = storeUrl("https://some-two-strings.com/space%20between", "1234")
        storeUrl("https://some-two-strings.com/20between", "abcd")

        val result = repository.search("%20", 1)

        assertEquals(1, result.results.count())
        assertUrlsEqual(expected, result.results.first())
    }

    @Test
    fun `when there are 21 URLs that match there are 3 pages`() {
        (1..21).forEach { i ->
            storeUrl("https://foobar.com/$i", "ab$i")
        }

        val result = repository.search("foobar", 1)

        assertEquals(3, result.paginationMetadata.totalPages)
    }

    @Test
    fun `when the search is for 'https' and the two URLs are 'http' and 'https' there is one result`() {
        storeUrl("http://example.com", "abcd")
        val https = storeUrl("https://example.com", "efgh")

        val result = repository.search("https", 1)

        assertEquals(1, result.results.count())
        assertUrlsEqual(https, result.results.first())
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
