package land.ver.url.shortener.unit.controllers

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import land.ver.url.shortener.controllers.AuditLogsController
import land.ver.url.shortener.repositories.AuditLogsRepository
import land.ver.url.shortener.repositories.dtos.AuditLogResponse
import land.ver.url.shortener.repositories.dtos.PagedResult
import land.ver.url.shortener.repositories.dtos.PaginationMetadata
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@WebMvcTest(AuditLogsController::class)
class AuditLogsControllerTest {
    companion object {
        private const val URL = "/api/v1/audit-logs?pageNumber=1"
    }

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var repository: AuditLogsRepository

    @Test
    fun `when there are no audit logs, the response status is OK`() {
        every { repository.getAll(1) }.returns(getEmptyResult())

        val request = get(URL).contentType(MediaType.APPLICATION_JSON)

        mvc
            .perform(request)
            .andExpect(status().isOk())
    }

    @Test
    fun `when there are no audit logs, the response body is correct`() {
        every { repository.getAll(1) }.returns(getEmptyResult())

        val request = get(URL).contentType(MediaType.APPLICATION_JSON)

        mvc
            .perform(request)
            .andExpect(
                content().string(
                    "{" +
                        "\"data\":[]," +
                        "\"paginationMetadata\":{\"pageNumber\":1,\"totalPages\":0,\"pageSize\":100}" +
                        "}"
                )
            )
    }

    private fun getEmptyResult() = PagedResult<AuditLogResponse>(
        results = emptyList(),
        paginationMetadata = PaginationMetadata(
            pageNumber = 1,
            totalPages = 0,
            pageSize = 100,
        )
    )
}
