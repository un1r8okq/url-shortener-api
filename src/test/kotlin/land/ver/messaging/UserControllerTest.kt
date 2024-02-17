package land.ver.messaging

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import land.ver.messaging.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest
class UserControllerTest(@Autowired private val mvc: MockMvc) {

    @MockkBean
    lateinit var userRepository: UserRepository

    @Test
    fun givenNoUsers_whenGetIndex_thenReturnsOk() {
        every { userRepository.findAll(ofType<Pageable>()) } returns PageImpl(emptyList())

        mvc.perform(
            get("/api/v1/users?pageNumber=1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isEmpty())
    }

    @Test
    fun givenMissingPageNumberParameter_whenGetIndex_thenReturnsError() {
        every { userRepository.findAll(ofType<Pageable>()) } returns PageImpl(emptyList())

        mvc.perform(
            get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Required parameter 'pageNumber' is not present."))
    }
}