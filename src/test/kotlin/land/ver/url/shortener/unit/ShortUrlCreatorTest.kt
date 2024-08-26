package land.ver.url.shortener.unit

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import land.ver.url.shortener.exceptions.MultipleUrlStubConflictException
import land.ver.url.shortener.models.UrlResponse
import land.ver.url.shortener.repositories.exceptions.UrlStubConflictException
import land.ver.url.shortener.services.NonRetryingShortUrlCreator
import land.ver.url.shortener.services.ShortUrlCreator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.Logger
import org.springframework.dao.DataIntegrityViolationException
import java.time.Instant
import java.util.UUID

class ShortUrlCreatorTest {
    private val nonRetryingShortUrlCreator: NonRetryingShortUrlCreator = mockk()
    private val logger: Logger = mockk()
    private val creator: ShortUrlCreator = ShortUrlCreator(nonRetryingShortUrlCreator, logger)

    @Test
    fun `returns result correctly`() {
        val longUrl = "https://example.com/a/b/c?q=123"
        val expectedResponse = getExampleResponse(longUrl)
        every { nonRetryingShortUrlCreator.create(longUrl) }.returns(expectedResponse)

        val result = creator.create(longUrl)

        assertEquals(expectedResponse, result)
    }

    @Test
    fun `calls nonRetryingCreator with longUrl`() {
        val longUrl = "https://example.com/a/b/c?q=123"
        every { nonRetryingShortUrlCreator.create(longUrl) }.returns(getExampleResponse(longUrl))

        creator.create(longUrl)

        verify(exactly = 1) { nonRetryingShortUrlCreator.create(longUrl) }
    }

    @Test
    fun `logs correctly on single failure`() {
        val longUrl = "https://example.com"
        every { logger.warn(any<String>(), any<Any>(), any<Any>()) } just runs
        every { nonRetryingShortUrlCreator.create(longUrl) }
            .answers {
                throw UrlStubConflictException("stub1", DataIntegrityViolationException(""))
            }
            .andThenAnswer {
                getExampleResponse(longUrl)
            }

        creator.create(longUrl)

        verify(exactly = 1) {
            logger.warn("Failed to save URL on attempt {} of 3 due to stub conflict using {}", 1, "stub1")
        }
    }

    @Test
    fun `when stub conflict occurs 3 times, throws exception`() {
        val longUrl = "https://example.com"
        every { logger.warn(any<String>(), any<Any>(), any<Any>()) } just runs
        every { nonRetryingShortUrlCreator.create(longUrl) }.throwsMany(
            listOf(
                UrlStubConflictException("stub1", DataIntegrityViolationException("")),
                UrlStubConflictException("stub2", DataIntegrityViolationException("")),
                UrlStubConflictException("stub3", DataIntegrityViolationException("")),
            )
        )

        assertThrows<MultipleUrlStubConflictException>(
            "Unable to create a URL due to URL stub conflicts after 3 attempts " +
                "with stubs \"stub1\", \"stub2\", \"stub1\""
        ) { creator.create(longUrl) }
    }

    private fun getExampleResponse(longUrl: String) = UrlResponse(
        id = UUID.fromString("01916711-7a04-70ab-b4cb-f19aeaeec82b"),
        longUrl = longUrl,
        stub = "stub",
        createdTimestampUtc = Instant.EPOCH,
        lastVisitedTimestampUtc = null,
    )
}
