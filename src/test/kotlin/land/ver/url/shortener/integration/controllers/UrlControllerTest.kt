package land.ver.url.shortener.integration.controllers

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import land.ver.url.shortener.controllers.UrlController
import land.ver.url.shortener.dtos.PagedApiResult
import land.ver.url.shortener.dtos.urls.UrlResponseDTO
import land.ver.url.shortener.mappers.UrlResponseMapper
import land.ver.url.shortener.repositories.AuditLogsRepository
import land.ver.url.shortener.repositories.UrlRepository
import land.ver.url.shortener.repositories.dtos.PagedResult
import land.ver.url.shortener.repositories.dtos.PaginationMetadata
import land.ver.url.shortener.repositories.dtos.UrlResponse
import land.ver.url.shortener.services.UrlStubGenerator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@WithMockUser
@ExtendWith(SpringExtension::class)
@WebMvcTest(UrlController::class)
class UrlControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var auditLogsRepository: AuditLogsRepository

    @MockkBean
    private lateinit var urlRepository: UrlRepository

    @MockkBean
    private lateinit var urlResponseMapper: UrlResponseMapper

    @MockkBean
    private lateinit var stubGenerator: UrlStubGenerator

    @Test
    fun `when there are no URLs, the response is correct`() {
        every { urlRepository.getAll(1) }.returns(getPagedResult(emptyList()))
        every { urlResponseMapper.map(getPagedResult(emptyList())) }.returns(
            PagedApiResult(
                data = emptyList(),
                paginationMetadata = land.ver.url.shortener.dtos.PaginationMetadata(
                    pageNumber = 1,
                    totalPages = 1,
                    pageSize = 100,
                ),
            ),
        )

        val request = get("/api/v1/urls?pageNumber=1").contentType(MediaType.APPLICATION_JSON)

        mvc
            .perform(request)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                    {
                        data: [],
                        paginationMetadata: {
                            pageNumber: 1,
                            totalPages: 1,
                            pageSize: 100
                        }
                    }    
                    """.trimIndent(),
                    true
                )
            )
    }

    @Test
    fun `when there is one URL, the response is correct`() {
        val repositoryResult = getPagedResult(
            listOf(
                UrlResponse(
                    id = UUID.fromString("01907523-499d-7218-be8a-3a058f0bf7dd"),
                    longUrl = "https://google.com",
                    stub = "abcd",
                    createdTimestampUtc = LocalDateTime.of(2024, 7, 2, 20, 30).toInstant(ZoneOffset.UTC),
                    lastVisitedTimestampUtc = LocalDateTime.of(2024, 7, 12, 20, 9).toInstant(ZoneOffset.UTC),
                )
            ),
        )
        every { urlRepository.getAll(1) }.returns(repositoryResult)
        every { urlResponseMapper.map(repositoryResult) }.returns(
            PagedApiResult(
                data = listOf(
                    UrlResponseDTO(
                        longUrl = "https://google.com",
                        shortenedUrl = "https://short.url/s/abcd",
                        createdTimestampUtc = "2024-07-02T20:30:00Z",
                        lastVisitTimestampUtc = "2024-07-12T20:09:00Z",
                    ),
                ),
                paginationMetadata = land.ver.url.shortener.dtos.PaginationMetadata(
                    pageNumber = 1,
                    totalPages = 1,
                    pageSize = 100,
                ),
            ),
        )

        val request = get("/api/v1/urls?pageNumber=1").contentType(MediaType.APPLICATION_JSON)

        mvc
            .perform(request)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                    {
                        data: [
                            {
                                longUrl: "https://google.com",
                                shortenedUrl: "https://short.url/s/abcd",
                                createdTimestampUtc: "2024-07-02T20:30:00Z",
                                lastVisitTimestampUtc: "2024-07-12T20:09:00Z"
                            }
                        ],
                        paginationMetadata: {
                            pageNumber: 1,
                            totalPages: 1,
                            pageSize: 100
                        }
                    }
                    """.trimIndent(),
                    true
                )
            )
    }

    @Test
    fun `when the pageNumber parameter is missing, the response is correct`() {
        val request = get("/api/v1/urls").contentType(MediaType.APPLICATION_JSON)

        mvc
            .perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(
                content().json(
                    """
                       {
                            type: "about:blank",
                            title: "Bad Request",
                            status: 400,
                            detail: "Required parameter 'pageNumber' is not present.",
                            instance: "/api/v1/urls"
                        }
                    """.trimIndent(),
                    true
                )
            )
    }

    @ParameterizedTest
    @ValueSource(longs = [-1, 0])
    fun `when the pageNumber parameter is not positive, a validation error is returned`(pageNumber: Long) {
        val request = get("/api/v1/urls?pageNumber=$pageNumber").contentType(MediaType.APPLICATION_JSON)

        mvc
            .perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(
                content().json(
                    """
                       {
                            type: "about:blank",
                            title: "Bad Request",
                            status: 400,
                            detail: "The pageNumber query parameter must be a positive integer.",
                            instance: "/api/v1/urls"
                        }
                    """.trimIndent(),
                    true
                )
            )
    }

    private fun getPagedResult(results: List<UrlResponse>) = PagedResult(
        results = results,
        paginationMetadata = PaginationMetadata(
            pageNumber = 1,
            totalPages = 1,
            pageSize = 100,
        )
    )
}
