package land.ver.url.shortener.services

import land.ver.url.shortener.LogType
import land.ver.url.shortener.URL_STUB_LENGTH
import land.ver.url.shortener.models.NewAuditLog
import land.ver.url.shortener.models.NewUrl
import land.ver.url.shortener.models.UrlResponse
import land.ver.url.shortener.repositories.AuditLogsRepository
import land.ver.url.shortener.repositories.UrlRepository
import land.ver.url.shortener.repositories.exceptions.UrlStubConflictException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NonRetryingShortUrlCreator(
    private val auditLogsRepository: AuditLogsRepository,
    private val urlRepository: UrlRepository,
    private val randomStringGenerator: RandomStringGenerator,
) {
    @Transactional
    fun create(longUrl: String): UrlResponse {
        require(longUrl.isNotBlank())

        val newUrl = NewUrl(
            longUrl = longUrl,
            stub = randomStringGenerator.generate(URL_STUB_LENGTH),
        )
        val url = saveUrl(newUrl)

        auditLogsRepository.save(
            NewAuditLog(
                LogType.URL_SHORTENED,
                "A short URL with the ID ${url.id}, " +
                    "stub ${url.stub}, " +
                    "and long URL ${trimStr(url.longUrl)} was created."
            )
        )

        return url
    }

    private fun saveUrl(newUrl: NewUrl): UrlResponse {
        try {
            val url = urlRepository.save(newUrl)

            return url
        } catch (ex: DataIntegrityViolationException) {
            throw UrlStubConflictException(newUrl.stub, ex)
        }
    }

    private fun trimStr(input: String): String {
        val ellipses = "..."

        if (input.length <= AUDIT_LOG_URL_MAX_LEN) {
            return input
        }

        return input.take(AUDIT_LOG_URL_MAX_LEN - ellipses.length).plus(ellipses)
    }
}
