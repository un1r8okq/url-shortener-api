package land.ver.url.shortener.controllers

import com.github.f4b6a3.uuid.UuidCreator
import land.ver.url.shortener.models.UrlVisit
import land.ver.url.shortener.repositories.UrlRepository
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/s")
class UrlRedirectController(val urlRepository: UrlRepository) {
    @GetMapping("/{stub}")
    fun index(@PathVariable stub: String): ResponseEntity<Any> {
        val url = urlRepository.findByStub(stub)

        if (url == null) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }

        val visit = UrlVisit(
            id = UuidCreator.getTimeBased(),
            timestampUtc = Instant.now(),
            url = url,
        )
        // TODO save URL visit

        val headers = HttpHeaders()
        headers.add("Location", url.longUrl)

        return ResponseEntity(headers, HttpStatus.FOUND)
    }
}
