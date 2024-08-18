package land.ver.url.shortener.controllers

import land.ver.url.shortener.LogType
import land.ver.url.shortener.models.NewAuditLog
import land.ver.url.shortener.repositories.AuditLogsRepository
import land.ver.url.shortener.repositories.UrlRepository
import land.ver.url.shortener.repositories.UrlVisitRepository
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/s")
class UrlRedirectController(
    private val auditLogsRepository: AuditLogsRepository,
    private val urlRepository: UrlRepository,
    private val urlVisitRepository: UrlVisitRepository,
) {
    @GetMapping("/{stub}")
    @Transactional
    fun index(@PathVariable stub: String): ResponseEntity<Any> {
        val url = urlRepository.findByStub(stub)

        if (url == null) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }

        urlVisitRepository.save(url.id)
        auditLogsRepository.save(
            NewAuditLog(LogType.URL_VISITED, "The URL $stub was visited.")
        )

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, url.longUrl)
            .build()
    }
}
