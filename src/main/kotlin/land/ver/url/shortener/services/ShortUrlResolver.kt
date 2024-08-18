package land.ver.url.shortener.services

import land.ver.url.shortener.LogType
import land.ver.url.shortener.models.NewAuditLog
import land.ver.url.shortener.repositories.AuditLogsRepository
import land.ver.url.shortener.repositories.UrlRepository
import land.ver.url.shortener.repositories.UrlVisitRepository

class ShortUrlResolver(
    private val urlRepository: UrlRepository,
    private val urlVisitRepository: UrlVisitRepository,
    private val auditLogsRepository: AuditLogsRepository,
) {
    fun resolve(stub: String): String? {
        val url = urlRepository.findByStub(stub)

        if (url != null) {
            urlVisitRepository.save(url.id)
            auditLogsRepository.save(
                NewAuditLog(
                    logType = LogType.URL_VISITED,
                    message = "The URL 'stub' was visited.",
                ),
            )
        }

        return url?.longUrl
    }
}
