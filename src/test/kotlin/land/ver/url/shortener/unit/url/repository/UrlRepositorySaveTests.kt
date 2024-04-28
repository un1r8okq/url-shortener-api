package land.ver.url.shortener.unit.url.repository

import land.ver.url.shortener.repositories.NewUrl
import land.ver.url.shortener.repositories.UrlRepository
import land.ver.url.shortener.unit.helpers.MockEntityManagerBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class UrlRepositorySaveTests {
    private val pageSize = 10L

    @Test
    fun testIdNotNull() {
        val entityManager = MockEntityManagerBuilder().build()
        val repository = UrlRepository(entityManager, pageSize)
        val newUrl = NewUrl(
            longUrl = "",
            stub = "",
        )

        val result = repository.save(newUrl)

        assertNotNull(result.id)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "",
            "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            "♨\uFE0F.com"
        ],
    )
    fun testStoringLongUrl(longUrl: String) {
        val entityManager = MockEntityManagerBuilder().build()
        val repository = UrlRepository(entityManager, pageSize)
        val newUrl = NewUrl(
            longUrl = longUrl,
            stub = "",
        )

        val result = repository.save(newUrl)

        assertEquals(longUrl, result.longUrl)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "",
            "e6SM",
            "qBwC"
        ],
    )
    fun testStoringStub(stub: String) {
        val entityManager = MockEntityManagerBuilder().build()
        val repository = UrlRepository(entityManager, pageSize)
        val newUrl = NewUrl(
            longUrl = "",
            stub = stub,
        )

        val result = repository.save(newUrl)

        assertEquals(stub, result.stub)
    }
}
