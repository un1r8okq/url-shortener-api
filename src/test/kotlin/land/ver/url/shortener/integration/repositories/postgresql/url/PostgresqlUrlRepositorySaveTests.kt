package land.ver.url.shortener.integration.repositories.postgresql.url

import land.ver.url.shortener.integration.repositories.postgresql.BaseRepositoryTest
import land.ver.url.shortener.models.NewUrl
import land.ver.url.shortener.repositories.postgresql.PostgresqlUrlRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException

class PostgresqlUrlRepositorySaveTests(
    @Autowired private val repository: PostgresqlUrlRepository,
) : BaseRepositoryTest() {
    @Test
    fun `the ID is generated correctly`() {
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
    fun `the URL is stored correctly`(longUrl: String) {
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
    fun `the stub is stored correctly`(stub: String) {
        val newUrl = NewUrl(
            longUrl = "",
            stub = stub,
        )

        val result = repository.save(newUrl)

        assertEquals(stub, result.stub)
    }

    @Test
    fun `when two URLs with the same stub are added, a DataIntegrityViolationException is thrown`() {
        val newUrl = NewUrl(
            longUrl = "",
            stub = "stub",
        )

        repository.save(newUrl)
        assertThrows<DataIntegrityViolationException> { repository.save(newUrl) }
    }
}
