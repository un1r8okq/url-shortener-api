package land.ver.url.shortener.integration.controllers

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import land.ver.url.shortener.controllers.UrlRedirectController
import land.ver.url.shortener.repositories.AuditLogsRepository
import land.ver.url.shortener.repositories.UrlRepository
import land.ver.url.shortener.repositories.UrlVisitRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus

@WithMockUser
@WebMvcTest(UrlRedirectController::class)
class UrlRedirectControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var urlRepo: UrlRepository

    @MockkBean
    private lateinit var urlVisitRepo: UrlVisitRepository

    @MockkBean
    private lateinit var auditLogsRepo: AuditLogsRepository

    @MockkBean
    private lateinit var transactionManager: PlatformTransactionManager

    @BeforeEach
    fun setup() {
        val transactionStatus = mockk<TransactionStatus>()

        every { transactionManager.getTransaction(any()) } returns transactionStatus
        every { transactionManager.commit(any()) } just runs
        every { transactionManager.rollback(any()) } just runs
    }

    @Test
    fun `when the stub doesn't exist, the response is 'not found'`() {
        every { urlRepo.findByStub("testStub") }.returns(null)

        val request = get("/s/testStub")

        mvc
            .perform(request)
            .andExpect(status().isNotFound())
            .andExpect(content().string(""))
    }
}
