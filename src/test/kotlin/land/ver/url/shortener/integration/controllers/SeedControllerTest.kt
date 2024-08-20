package land.ver.url.shortener.integration.controllers

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import land.ver.url.shortener.controllers.SeedController
import land.ver.url.shortener.models.UrlResponse
import land.ver.url.shortener.services.RandomNumberSource
import land.ver.url.shortener.services.RandomStringGenerator
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
@WebMvcTest(SeedController::class)
class SeedControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var urlCreator: ShortUrlCreator

    @MockkBean
    private lateinit var randomNumberSource: RandomNumberSource

    @MockkBean
    private lateinit var randomStringGenerator: RandomStringGenerator

    @Test
    fun `when the seed endpoint is hit, the response is correct`() {
        setupMocks()

        val request = post("/api/v1/seed")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf().asHeader())
            .content(
                """
                { "urlCount": 1 }
                """.trimIndent()
            )

        mvc
            .perform(request)
            .andExpect(status().isOk)
            .andExpect { content().string("") }
    }

    @Test
    fun `when CSRF token is invalid, response status is Forbidden`() {
        val request = post("/api/v1/seed")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf().useInvalidToken())
            .content(
                """
                { "urlCount": 1 }
                """.trimIndent()
            )

        mvc
            .perform(request)
            .andExpect(status().isForbidden)
    }

    private fun setupMocks() {
        every { urlCreator.create(any()) } returns (
            UrlResponse(
                id = UUID.fromString("01914d64-f165-7942-80bc-9eddbb973bdb"),
                longUrl = "https://example.com",
                stub = "abcd",
                createdTimestampUtc = LocalDateTime.of(2024, 8, 14, 20, 16).toInstant(ZoneOffset.UTC),
                lastVisitedTimestampUtc = null,
            )
            )
        every { randomNumberSource.getRandomNumber(any(), any()) } returns (0)
        every { randomStringGenerator.generate(any(), any()) } returns ("abcd1234")
    }
}
