package land.ver.url.shortener.integration.controllers

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import land.ver.url.shortener.controllers.UrlController
import land.ver.url.shortener.dtos.urls.UrlResponseDTO
import land.ver.url.shortener.mappers.UrlResponseMapper
import land.ver.url.shortener.models.NewUrl
import land.ver.url.shortener.models.UrlResponse
import land.ver.url.shortener.repositories.UrlRepository
import land.ver.url.shortener.services.ShortUrlCreator
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
    private lateinit var urlCreator: ShortUrlCreator

    @MockkBean
    private lateinit var urlRepository: UrlRepository

    @MockkBean
    private lateinit var urlResponseMapper: UrlResponseMapper

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
        every { urlCreator.create("https://example.com") } returns (
            UrlResponse(
                id = UUID.fromString("01914d64-f165-7942-80bc-9eddbb973bdb"),
                longUrl = "https://example.com",
                stub = "abcd",
                createdTimestampUtc = LocalDateTime.of(2024, 8, 14, 20, 16).toInstant(ZoneOffset.UTC),
                lastVisitedTimestampUtc = null,
            )
            )
        val urlResponse = UrlResponse(
            id = UUID.fromString("01914d64-f165-7942-80bc-9eddbb973bdb"),
            longUrl = "https://example.com",
            stub = "abcd",
            createdTimestampUtc = LocalDateTime.of(2024, 8, 14, 20, 16).toInstant(ZoneOffset.UTC),
            lastVisitedTimestampUtc = null,
        )
        every {
            urlRepository.save(NewUrl("https://example.com", "abcd"))
        } returns (urlResponse)
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
