package land.ver.url.shortener.services

import land.ver.url.shortener.exceptions.MultipleUrlStubConflictException
import land.ver.url.shortener.models.UrlResponse
import land.ver.url.shortener.repositories.exceptions.UrlStubConflictException
import org.slf4j.Logger
import org.springframework.stereotype.Service

const val URL_CREATION_RETRY_COUNT = 3
const val AUDIT_LOG_URL_MAX_LEN = 32

@Service
class ShortUrlCreator(
    private val nonRetryingCreator: NonRetryingShortUrlCreator,
    private val logger: Logger,
) {
    fun create(longUrl: String): UrlResponse {
        val stubsAttempted = mutableListOf<String>()

        repeat(URL_CREATION_RETRY_COUNT) { attemptNum ->
            try {
                return nonRetryingCreator.create(longUrl)
            } catch (ex: UrlStubConflictException) {
                stubsAttempted.add(ex.stub)
                logger.warn(
                    "Failed to save URL on attempt {} of $URL_CREATION_RETRY_COUNT due to stub conflict using {}",
                    attemptNum + 1,
                    ex.stub,
                )
            }
        }

        throw MultipleUrlStubConflictException(
            attempts = URL_CREATION_RETRY_COUNT,
            stubsAttempted = stubsAttempted,
        )
    }
}
