package land.ver.url.shortener.integration.controllers

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import land.ver.url.shortener.LogType
import land.ver.url.shortener.controllers.AuditLogsController
import land.ver.url.shortener.repositories.AuditLogsRepository
import land.ver.url.shortener.repositories.dtos.AuditLogResponse
import land.ver.url.shortener.repositories.dtos.PagedResult
import land.ver.url.shortener.repositories.dtos.PaginationMetadata
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@ExtendWith(SpringExtension::class)
@WebMvcTest(AuditLogsController::class)
class AuditLogsControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var repository: AuditLogsRepository

    @Test
    fun `when there are no audit logs, the response is correct`() {
        every { repository.getAll(1) }.returns(getPagedResult(emptyList()))

        val request = get("/api/v1/audit-logs?pageNumber=1").contentType(MediaType.APPLICATION_JSON)

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
    fun `when there is one audit log, the response is correct`() {
        every { repository.getAll(1) }
            .returns(
                getPagedResult(
                    listOf(
                        AuditLogResponse(
                            id = UUID.fromString("01907523-499d-7218-be8a-3a058f0bf7dd"),
                            createdTimestampUtc = LocalDateTime.of(2024, 7, 2, 20, 30).toInstant(ZoneOffset.UTC),
                            logType = LogType.URL_SHORTENED,
                            message = "A URL was shortened",
                        )
                    ),
                ),
            )

        val request = get("/api/v1/audit-logs?pageNumber=1").contentType(MediaType.APPLICATION_JSON)

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
                                id: "01907523-499d-7218-be8a-3a058f0bf7dd",
                                createdTimestampUtc: "2024-07-02T20:30:00Z",
                                logType: "url_shortened",
                                message: "A URL was shortened"
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
        val request = get("/api/v1/audit-logs").contentType(MediaType.APPLICATION_JSON)

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
                            instance: "/api/v1/audit-logs"
                        }
                    """.trimIndent(),
                    true
                )
            )
    }

    @ParameterizedTest
    @ValueSource(longs = [-1, 0])
    fun `when the pageNumber parameter is not positive, a validation error is returned`(pageNumber: Long) {
        val request = get("/api/v1/audit-logs?pageNumber=$pageNumber").contentType(MediaType.APPLICATION_JSON)

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
                            instance: "/api/v1/audit-logs"
                        }
                    """.trimIndent(),
                    true
                )
            )
    }

    private fun getPagedResult(results: List<AuditLogResponse>) = PagedResult(
        results = results,
        paginationMetadata = PaginationMetadata(
            pageNumber = 1,
            totalPages = 1,
            pageSize = 100,
        )
    )
}
