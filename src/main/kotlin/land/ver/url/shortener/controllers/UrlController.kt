package land.ver.url.shortener.controllers

import jakarta.validation.Valid
import jakarta.validation.constraints.PositiveOrZero
import land.ver.url.shortener.dtos.PagedApiResult
import land.ver.url.shortener.dtos.urls.ShortenUrlRequest
import land.ver.url.shortener.dtos.urls.UrlResponseDTO
import land.ver.url.shortener.mappers.UrlResponseMapper
import land.ver.url.shortener.repositories.UrlRepository
import land.ver.url.shortener.repositories.dtos.NewUrl
import land.ver.url.shortener.services.StubGeneratorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/urls")
class UrlController(
    private val urlRepository: UrlRepository,
    private val stubGenerator: StubGeneratorService,
) {
    @GetMapping("", "/")
    fun index(@Valid @PositiveOrZero @RequestParam pageNumber: Long): PagedApiResult<UrlResponseDTO> {
        val urls = urlRepository.getAll(pageNumber)

        return UrlResponseMapper().map(urls)
    }

    @PostMapping("")
    fun create(@Valid @RequestBody shortenUrlRequest: ShortenUrlRequest): ResponseEntity<UrlResponseDTO> {
        val newUrl = NewUrl(
            longUrl = shortenUrlRequest.longUrl,
            stub = stubGenerator.generate(),
        )
        val url = urlRepository.save(newUrl)

        return ResponseEntity(UrlResponseMapper().map(url), HttpStatus.CREATED)
    }
}
