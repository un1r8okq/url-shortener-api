package land.ver.url.shortener.unit

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import land.ver.url.shortener.services.RandomNumberSource
import land.ver.url.shortener.services.RandomStringGenerator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class RandomStringGeneratorTest {
    private val rand = mockk<RandomNumberSource>()
    private val service = RandomStringGenerator(rand)

    init {
        every { rand.getRandomNumber(any(), any()) } returns 0
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 10, 64])
    fun `the stub is of the correct length`(length: Int) {
        val result = service.generate(length)

        assertEquals(length, result.length)
    }

    @Test
    fun `the random number source is called correctly`() {
        service.generate(4)

        verify(exactly = 4) { rand.getRandomNumber(0, 64) }
    }

    @Test
    fun `when the random number provider always returns 0, the generated stub is 'aaaa'`() {
        val result = service.generate(4)

        assertEquals("aaaa", result)
    }

    @Test
    fun `when the random number provider always returns 63, the generated stub is '____'`() {
        every { rand.getRandomNumber(any(), any()) } returns 63

        val result = service.generate(4)

        assertEquals("____", result)
    }

    @Test
    fun `when the random number provider returns '2, 3, 5, 7', the generated stub is 'cdfh'`() {
        every { rand.getRandomNumber(any(), any()) } returnsMany listOf(2, 3, 5, 7)

        val result = service.generate(4)

        assertEquals("cdfh", result)
    }

    @ParameterizedTest
    @ValueSource(ints = [Int.MIN_VALUE, -100, -1])
    fun `when length is negative, an invalid argument exception is thrown`(length: Int) {
        assertThrows<IllegalArgumentException> { service.generate(length) }
    }

    @ParameterizedTest
    @ValueSource(ints = [65, 100, Int.MAX_VALUE])
    fun `when length is greater than 64, an invalid argument exception is thrown`(length: Int) {
        assertThrows<IllegalArgumentException> { service.generate(length) }
    }
}
