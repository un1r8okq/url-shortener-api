package land.ver.url.shortener.unit.url.repository

import land.ver.url.shortener.repositories.UrlRepository
import land.ver.url.shortener.repositories.dtos.NewUrl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired

class UrlRepositorySaveTests(
    @Autowired private val urlRepository: UrlRepository,
) : BaseRepositoryTest() {
    @Test
    fun testIdNotNull() {
        val newUrl = NewUrl(
            longUrl = "",
            stub = "",
        )

        val result = urlRepository.save(newUrl)

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
        val newUrl = NewUrl(
            longUrl = longUrl,
            stub = "",
        )

        val result = urlRepository.save(newUrl)

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
        val newUrl = NewUrl(
            longUrl = "",
            stub = stub,
        )

        val result = urlRepository.save(newUrl)

        assertEquals(stub, result.stub)
    }
}
