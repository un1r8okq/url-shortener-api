package land.ver.url.shortener.unit

import com.github.f4b6a3.uuid.UuidCreator
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import land.ver.url.shortener.LogType
import land.ver.url.shortener.models.AuditLogResponse
import land.ver.url.shortener.models.NewAuditLog
import land.ver.url.shortener.models.UrlResponse
import land.ver.url.shortener.models.UrlVisitResponse
import land.ver.url.shortener.repositories.AuditLogsRepository
import land.ver.url.shortener.repositories.UrlRepository
import land.ver.url.shortener.repositories.UrlVisitRepository
import land.ver.url.shortener.services.ShortUrlResolver
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Clock
import java.time.Instant
import java.util.UUID

@ExtendWith(MockKExtension::class)
class ShortUrlResolverTest {
    private val urlId: UUID = UuidCreator.getTimeOrderedEpoch()

    @MockK
    private lateinit var auditLogsRepository: AuditLogsRepository

    @MockK
    private lateinit var urlRepository: UrlRepository

    @MockK
    private lateinit var urlVisitRepository: UrlVisitRepository

    @InjectMockKs
    private lateinit var resolver: ShortUrlResolver

    @BeforeEach
    fun setup() {
        setupAuditRepository()
        setupUrlVisitRepository()
    }

    @Test
    fun `when the stub does not match, the resolver returns null`() {
        setupUrlRepository("stub", null)

        val result = resolver.resolve("stub")

        Assertions.assertNull(result)
    }

    @Test
    fun `when the stub matches, the resolver returns the long URL`() {
        setupUrlRepository("stub", "https://example.com")

        val result = resolver.resolve("stub")

        Assertions.assertEquals("https://example.com", result)
    }

    @Test
    fun `when the stub matches, 'last visited' is updated`() {
        setupUrlRepository("stub", "https://example.com")

        resolver.resolve("stub")

        verify(exactly = 1) { urlVisitRepository.save(urlId) }
    }

    @Test
    fun `when the stub matches, audit log is written`() {
        setupUrlRepository("stub", "https://example.com")
        val expectedLog = NewAuditLog(
            logType = LogType.URL_VISITED,
            message = "The URL 'stub' was visited.",
        )

        resolver.resolve("stub")

        verify(exactly = 1) { auditLogsRepository.save(expectedLog) }
    }

    private fun setupUrlRepository(stub: String, longUrl: String?) {
        val urlResponse = longUrl?.let {
            UrlResponse(
                id = urlId,
                longUrl = longUrl,
                stub = stub,
                createdTimestampUtc = Instant.now(Clock.systemUTC()),
                lastVisitedTimestampUtc = null,
            )
        }

        every { urlRepository.findByStub(stub) }.returns(urlResponse)
    }

    private fun setupAuditRepository() {
        val newAuditLog = NewAuditLog(
            logType = LogType.URL_VISITED,
            message = "The URL 'stub' was visited.",
        )
        val response = AuditLogResponse(
            id = UuidCreator.getTimeOrderedEpoch(),
            createdTimestampUtc = Instant.now(),
            logType = LogType.URL_VISITED,
            message = "The URL 'stub' was visited.",
        )
        every { auditLogsRepository.save(newAuditLog) } returns response
    }

    private fun setupUrlVisitRepository() {
        every { urlVisitRepository.save(urlId) } returns UrlVisitResponse(
            id = UuidCreator.getTimeOrderedEpoch(),
            urlId = urlId,
            timestampUtc = Instant.now(),
        )
    }
}
