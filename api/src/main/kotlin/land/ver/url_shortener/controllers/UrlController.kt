package land.ver.url_shortener.controllers

import land.ver.url_shortener.StubGeneratorService
import land.ver.url_shortener.UrlRepository
import land.ver.url_shortener.dtos.ShortenUrlRequest
import land.ver.url_shortener.dtos.UrlResponse
import land.ver.url_shortener.models.Url
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/urls")
class UrlController(
    private val urlRepository: UrlRepository,
    private val stubGenerator: StubGeneratorService,
) {
    @PostMapping("")
    fun create(@RequestBody shortenUrlRequest: ShortenUrlRequest): ResponseEntity<UrlResponse> {
        val url = urlRepository.save(Url(
            id = null,
            longUrl = shortenUrlRequest.longUrl,
            stub = stubGenerator.generate(),
        ))

        val response = UrlResponse(
            longUrl = url.longUrl,
            shortenedUrl = "http://192.168.1.5:8080/s/" + url.stub,
        )

        return ResponseEntity(response, HttpStatus.CREATED)
    }
}
