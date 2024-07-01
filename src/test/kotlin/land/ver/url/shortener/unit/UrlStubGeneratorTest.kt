package land.ver.url.shortener.unit

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import land.ver.url.shortener.services.RandomNumberSource
import land.ver.url.shortener.services.UrlStubGenerator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UrlStubGeneratorTest {
    @Test
    fun `the stub is of the correct length`() {
        val rand = mockk<RandomNumberSource>()
        every { rand.getRandomNumber(any(), any()) } returns 0
        val service = UrlStubGenerator(rand)

        val result = service.generate()

        assertEquals(4, result.length)
    }

    @Test
    fun `the random number source is called correctly`() {
        val rand = mockk<RandomNumberSource>()
        every { rand.getRandomNumber(any(), any()) } returns 0
        val service = UrlStubGenerator(rand)

        service.generate()

        verify(exactly = 4) { rand.getRandomNumber(0, 64) }
    }

    @Test
    fun `when the random number provider always returns 0, the generated stub is 'aaaa'`() {
        val rand = mockk<RandomNumberSource>()
        every { rand.getRandomNumber(any(), any()) } returns 0
        val service = UrlStubGenerator(rand)

        val result = service.generate()

        assertEquals("aaaa", result)
    }

    @Test
    fun `when the random number provider always returns 63, the generated stub is '____'`() {
        val rand = mockk<RandomNumberSource>()
        every { rand.getRandomNumber(any(), any()) } returns 63
        val service = UrlStubGenerator(rand)

        val result = service.generate()

        assertEquals("____", result)
    }

    @Test
    fun `when the random number provider returns '2, 3, 5, 7', the generated stub is 'cdfh'`() {
        val rand = mockk<RandomNumberSource>()
        every { rand.getRandomNumber(any(), any()) } returnsMany listOf(2, 3, 5, 7)
        val service = UrlStubGenerator(rand)

        val result = service.generate()

        assertEquals("cdfh", result)
    }
}
