package land.ver.url.shortener.controllers

import land.ver.url.shortener.LogType
import land.ver.url.shortener.repositories.AuditLogsRepository
import land.ver.url.shortener.repositories.UrlRepository
import land.ver.url.shortener.repositories.UrlVisitRepository
import land.ver.url.shortener.repositories.dtos.NewAuditLog
import land.ver.url.shortener.repositories.dtos.NewUrlVisit
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/s")
class UrlRedirectController(
    private val urlRepository: UrlRepository,
    private val urlVisitRepository: UrlVisitRepository,
    private val auditLogsRepository: AuditLogsRepository,
) {
    @GetMapping("/{stub}")
    @Transactional
    fun index(@PathVariable stub: String): ResponseEntity<Any> {
        val url = urlRepository.findByStub(stub)

        if (url == null) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }

        urlVisitRepository.save(
            NewUrlVisit(
                timestampUtc = Instant.now(),
                url = url,
            )
        )
        auditLogsRepository.save(
            NewAuditLog(LogType.URL_VISITED, "The URL $stub was visited.")
        )

        val headers = HttpHeaders()
        headers.add("Location", url.longUrl)

        return ResponseEntity(headers, HttpStatus.FOUND)
    }
}
