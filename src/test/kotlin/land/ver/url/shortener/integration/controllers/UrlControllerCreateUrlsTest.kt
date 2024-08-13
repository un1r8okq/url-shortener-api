package land.ver.url.shortener.integration.controllers

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import land.ver.url.shortener.LogType
import land.ver.url.shortener.controllers.UrlController
import land.ver.url.shortener.dtos.urls.UrlResponseDTO
import land.ver.url.shortener.mappers.UrlResponseMapper
import land.ver.url.shortener.repositories.AuditLogsRepository
import land.ver.url.shortener.repositories.UrlRepository
import land.ver.url.shortener.repositories.dtos.AuditLogResponse
import land.ver.url.shortener.repositories.dtos.NewAuditLog
import land.ver.url.shortener.repositories.dtos.NewUrl
import land.ver.url.shortener.repositories.dtos.UrlResponse
import land.ver.url.shortener.services.UrlStubGenerator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@WithMockUser
@ExtendWith(SpringExtension::class)
@WebMvcTest(UrlController::class)
class UrlControllerCreateUrlsTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    @Suppress("UnusedPrivateProperty")
    private lateinit var auditLogsRepository: AuditLogsRepository

    @MockkBean
    private lateinit var urlRepository: UrlRepository

    @MockkBean
    private lateinit var urlResponseMapper: UrlResponseMapper

    @MockkBean
    @Suppress("UnusedPrivateProperty")
    private lateinit var stubGenerator: UrlStubGenerator

    @MockkBean
    private lateinit var transactionManager: PlatformTransactionManager

    @Test
    fun `when URL is created, the response is correct`() {
        setupMocks()

        val request = post("/api/v1/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf().asHeader())
            .content(
                """
                { "longUrl": "https://example.com" }
                """.trimIndent()
            )

        mvc
            .perform(request)
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                        {
                        "longUrl": "https://example.com",
                        "shortenedUrl": "https://urlshortener.example.com/s/abcd",
                        "createdTimestampUtc": "2024-08-14T20:16:00Z",
                        "lastVisitTimestampUtc": null
                        }
                    """.trimIndent(),
                    true,
                )
            )
    }

    @Test
    fun `when CSRF token is invalid, response status is Forbidden`() {
        val request = post("/api/v1/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf().useInvalidToken())
            .content(
                """
                { "longUrl": "https://example.com" }
                """.trimIndent()
            )

        mvc
            .perform(request)
            .andExpect(status().isForbidden)
    }

    private fun setupMocks() {
        val urlResponse = UrlResponse(
            id = UUID.fromString("01914d64-f165-7942-80bc-9eddbb973bdb"),
            longUrl = "https://example.com",
            stub = "abcd",
            createdTimestampUtc = LocalDateTime.of(2024, 8, 14, 20, 16).toInstant(ZoneOffset.UTC),
            lastVisitedTimestampUtc = null,
        )
        val transactionStatus = mockk<TransactionStatus>()
        every { transactionManager.getTransaction(any()) } returns transactionStatus
        every { transactionManager.commit(any()) } just runs
        every { transactionManager.rollback(any()) } just runs
        every { stubGenerator.generate() } returns ("abcd")
        every {
            urlRepository.save(NewUrl("https://example.com", "abcd"))
        } returns (urlResponse)
        every {
            auditLogsRepository.save(
                NewAuditLog(
                    LogType.URL_SHORTENED,
                    "A short URL with the ID 01914d64-f165-7942-80bc-9eddbb973bdb, " +
                        "stub abcd, and long URL https://example.com was created.",
                )
            )
        } returns AuditLogResponse(
            id = UUID.fromString("01914d67-d583-7a75-a4d4-eeb24e3745eb"),
            createdTimestampUtc = LocalDateTime.of(2024, 8, 14, 20, 22).toInstant(ZoneOffset.UTC),
            logType = LogType.URL_SHORTENED,
            message = "A short URL with the ID 01914d64-f165-7942-80bc-9eddbb973bdb, " +
                "stub abcd, and long URL https://example.com was created.",
        )
        every { urlResponseMapper.map(urlResponse) } returns (
            UrlResponseDTO(
                longUrl = "https://example.com",
                shortenedUrl = "https://urlshortener.example.com/s/abcd",
                createdTimestampUtc = "2024-08-14T20:16:00Z",
                lastVisitTimestampUtc = null,
            )
            )
    }
}
