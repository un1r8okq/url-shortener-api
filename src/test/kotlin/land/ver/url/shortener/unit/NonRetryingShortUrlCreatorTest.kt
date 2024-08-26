package land.ver.url.shortener.unit

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import land.ver.url.shortener.LogType
import land.ver.url.shortener.models.AuditLogResponse
import land.ver.url.shortener.models.NewAuditLog
import land.ver.url.shortener.models.NewUrl
import land.ver.url.shortener.models.UrlResponse
import land.ver.url.shortener.repositories.AuditLogsRepository
import land.ver.url.shortener.repositories.UrlRepository
import land.ver.url.shortener.repositories.exceptions.UrlStubConflictException
import land.ver.url.shortener.services.NonRetryingShortUrlCreator
import land.ver.url.shortener.services.RandomStringGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.dao.DataIntegrityViolationException
import java.time.Instant
import java.util.UUID

class NonRetryingShortUrlCreatorTest {
    private val mockAuditLogsRepository: AuditLogsRepository = mockk()
    private val mockUrlRepository: UrlRepository = mockk()
    private val mockStubGenerator: RandomStringGenerator = mockk()
    private val creator: NonRetryingShortUrlCreator = NonRetryingShortUrlCreator(
        mockAuditLogsRepository,
        mockUrlRepository,
        mockStubGenerator,
    )

    init {
        every { mockAuditLogsRepository.save(any()) } returns (
            AuditLogResponse(
                UUID.randomUUID(),
                Instant.MIN,
                LogType.URL_SHORTENED,
                "",
            )
            )
        every { mockUrlRepository.save(any()) } returns (
            UrlResponse(
                UUID.randomUUID(),
                "",
                "",
                Instant.MIN,
                null,
            )
            )
        every { mockStubGenerator.generate(any()) } returns("1234")
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "http://example.com",
            "https://example.com",
            "https://example.com/some/path",
            "https://example.com/some/path?a=1&b=2",
        ]
    )
    fun `the long URL is returned correctly`(longUrl: String) {
        setupMockUrlRepoToReturn(longUrl, "abcd")
        val result = creator.create(longUrl)

        Assertions.assertEquals(longUrl, result.longUrl)
    }

    @Test
    fun `when the long URL is longer than 32 chars, it is trimmed in the audit logs`() {
        setupMockUrlRepoToReturn("https://example.com/some/really/long/url", "abcd")
        val expectedAuditLog = NewAuditLog(
            LogType.URL_SHORTENED,
            "A short URL with the ID 01916711-7a04-70ab-b4cb-f19aeaeec82b, " +
                "stub abcd, " +
                "and long URL https://example.com/some/real... was created."
        )

        creator.create("https://example.com/some/really/long/url")

        verify(exactly = 1) { mockAuditLogsRepository.save(expectedAuditLog) }
    }

    @Test
    fun `when a long URL is empty, IllegalArgumentException is thrown`() {
        assertThrows<IllegalArgumentException>("longUrl must not be empty") {
            creator.create("")
        }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "",
            "abcd",
            "1234",
        ]
    )
    fun `the stub from the stub generator is used`(generatedStub: String) {
        every { mockStubGenerator.generate(any()) } returns (generatedStub)
        val longUrl = "https://example.com"
        setupMockUrlRepoToReturn(longUrl, generatedStub)
        val result = creator.create(longUrl)

        Assertions.assertEquals(generatedStub, result.stub)
    }

    @Test
    fun `the correct data is passed to the URL repository`() {
        val longurl = "https://example.com"
        val stub = "abcd"
        every { mockStubGenerator.generate(any()) } returns (stub)
        setupMockUrlRepoToReturn(longurl, stub)

        creator.create(longurl)

        verify(exactly = 1) { mockUrlRepository.save(NewUrl(longurl, stub)) }
    }

    @Test
    fun `the correct data is passed to the audit log repository`() {
        setupMockUrlRepoToReturn("https://example.com", "abcd")
        val expectedAuditLog = NewAuditLog(
            LogType.URL_SHORTENED,
            "A short URL with the ID 01916711-7a04-70ab-b4cb-f19aeaeec82b, " +
                "stub abcd, " +
                "and long URL https://example.com was created."
        )

        creator.create("https://example.com")

        verify(exactly = 1) { mockAuditLogsRepository.save(expectedAuditLog) }
    }

    @Test
    fun `audit logs are not written if saving URL fails`() {
        every { mockUrlRepository.save(any()) }.throws(RuntimeException("Database error"))

        assertThrows<RuntimeException> { creator.create("https://example.com") }

        verify(exactly = 1) { mockUrlRepository.save(any()) }
        verify(exactly = 0) { mockAuditLogsRepository.save(any()) }
    }

    @Test fun `when a URL stub is already in use, it throws a UrlStubConflictException`() {
        every { mockUrlRepository.save(any()) }.throws(
            UrlStubConflictException("stub1", DataIntegrityViolationException("")),
        )
        every { mockStubGenerator.generate(any()) } returns("stub1")

        val ex = assertThrows<UrlStubConflictException> { creator.create("https://example.com") }

        assertEquals("The URL stub \"stub1\" already exists.", ex.message)
        verify(exactly = 1) { mockUrlRepository.save(NewUrl("https://example.com", "stub1")) }
    }

    private fun setupMockUrlRepoToReturn(longUrl: String, stub: String) {
        every { mockUrlRepository.save(any()) } returns (
            UrlResponse(
                id = UUID.fromString("01916711-7a04-70ab-b4cb-f19aeaeec82b"),
                longUrl = longUrl,
                stub = stub,
                createdTimestampUtc = Instant.EPOCH,
                lastVisitedTimestampUtc = null,
            )
            )
    }
}
