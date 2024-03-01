package land.ver.url_shortener.dtos

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ShortenUrlRequestTest() {

    private var validator: Validator

    init {
        val validatorFactory = Validation.buildDefaultValidatorFactory()
        validator = validatorFactory.validator
    }

    @Test
    fun testEmptyUrl() {
        val dto = ShortenUrlRequest("")

        val violations = validator.validate(dto)

        assertEquals(1, violations.count())
        assertEquals("must not be empty", violations.toList()[0].message)
    }

    @Test
    fun testNonUrl() {
        val dto = ShortenUrlRequest("not a URL")

        val violations = validator.validate(dto)

        assertEquals(1, violations.count())
        assertEquals("must be a valid URL", violations.toList()[0].message)
    }
}