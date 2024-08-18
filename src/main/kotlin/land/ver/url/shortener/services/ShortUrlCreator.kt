package land.ver.url.shortener.services

import jakarta.transaction.Transactional
import land.ver.url.shortener.LogType
import land.ver.url.shortener.exceptions.InvalidUrlException
import land.ver.url.shortener.models.NewAuditLog
import land.ver.url.shortener.models.NewUrl
import land.ver.url.shortener.models.UrlResponse
import land.ver.url.shortener.repositories.AuditLogsRepository
import land.ver.url.shortener.repositories.UrlRepository
import org.springframework.stereotype.Service

@Service
class ShortUrlCreator(
    private val auditLogsRepository: AuditLogsRepository,
    private val urlRepository: UrlRepository,
    private val urlStubGenerator: UrlStubGenerator,
) {
    @Transactional
    fun create(longUrl: String): UrlResponse {
        if (longUrl == "") {
            throw InvalidUrlException()
        }

        val url = urlRepository.save(
            NewUrl(
                longUrl = longUrl,
                stub = urlStubGenerator.generate(),
            ),
        )

        auditLogsRepository.save(
            NewAuditLog(
                LogType.URL_SHORTENED,
                "A short URL with the ID ${url.id}, " +
                    "stub ${url.stub}, " +
                    "and long URL ${limitStrLen(url.longUrl)} was created."
            )
        )

        return url
    }

    private fun limitStrLen(input: String): String {
        @Suppress("MagicNumber")
        val maxLen = 32

        @Suppress("MagicNumber")
        val ellipsisLen = 3

        if (input.length <= maxLen) {
            return input
        }

        return input.take(maxLen - ellipsisLen).plus("...")
    }
}
