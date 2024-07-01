package land.ver.url.shortener.unit.dtos

import jakarta.validation.Validation
import jakarta.validation.Validator
import land.ver.url.shortener.dtos.urls.ShortenUrlRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ShortenUrlRequestTest {

    private var validator: Validator

    init {
        val validatorFactory = Validation.buildDefaultValidatorFactory()
        validator = validatorFactory.validator
    }

    @Test
    fun `when the URL is empty, the correct violation is returned`() {
        val dto = ShortenUrlRequest("")

        val violations = validator.validate(dto)

        assertEquals(1, violations.count())
        assertEquals("must not be empty", violations.toList()[0].message)
    }

    @Test
    fun `when the URL is not a URL, the correct violation is returned`() {
        val dto = ShortenUrlRequest("not a URL")

        val violations = validator.validate(dto)

        assertEquals(1, violations.count())
        assertEquals("must be a valid URL", violations.toList()[0].message)
    }
}
