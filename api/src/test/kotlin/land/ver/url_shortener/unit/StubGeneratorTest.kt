package land.ver.url_shortener.unit

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import land.ver.url_shortener.RandomNumberService
import land.ver.url_shortener.StubGeneratorService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StubGeneratorTest {
    @Test
    fun testResultLength() {
        val rand = mockk<RandomNumberService>()
        every { rand.getRandomNumber(any(), any())} returns 0
        val service = StubGeneratorService(rand)

        val result = service.generate()

        assertEquals(4, result.length)
    }

    @Test
    fun testRandomCalledCorrectly() {
        val rand = mockk<RandomNumberService>()
        every { rand.getRandomNumber(any(), any()) } returns 0
        val service = StubGeneratorService(rand)

        service.generate()

        verify(exactly = 4) { rand.getRandomNumber(0, 64) }
    }
    @Test
    fun testStartOfAlphabet() {
        val rand = mockk<RandomNumberService>()
        every { rand.getRandomNumber(any(), any())} returns 0
        val service = StubGeneratorService(rand)

        val result = service.generate()

        assertEquals("aaaa", result)
    }

    @Test
    fun testEndOfAlphabet() {
        val rand = mockk<RandomNumberService>()
        every { rand.getRandomNumber(any(), any())} returns 63
        val service = StubGeneratorService(rand)

        val result = service.generate()

        assertEquals("____", result)
    }

    @Test
    fun testMix() {
        val rand = mockk<RandomNumberService>()
        every { rand.getRandomNumber(any(), any())} returnsMany listOf(2, 3, 5, 7)
        val service = StubGeneratorService(rand)

        val result = service.generate()

        assertEquals("cdfh", result)
    }
}
