package land.ver.url.shortener.controllers

import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import land.ver.url.shortener.dtos.PagedApiResult
import land.ver.url.shortener.dtos.urls.ShortenUrlRequest
import land.ver.url.shortener.dtos.urls.UrlResponseDTO
import land.ver.url.shortener.mappers.UrlResponseMapper
import land.ver.url.shortener.repositories.UrlRepository
import land.ver.url.shortener.services.ShortUrlCreator
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
    private val urlCreator: ShortUrlCreator,
    private val urlRepository: UrlRepository,
    private val responseMapper: UrlResponseMapper,
) {
    @GetMapping
    fun index(
        @Valid
        @RequestParam
        @Positive(message = PAGE_NUM_MUST_BE_POSITIVE)
        pageNumber: Long
    ): PagedApiResult<UrlResponseDTO> {
        val urls = urlRepository.getAll(pageNumber)

        return responseMapper.map(urls)
    }

    @PostMapping
    fun create(@Valid @RequestBody shortenUrlRequest: ShortenUrlRequest): ResponseEntity<UrlResponseDTO> {
        val url = urlCreator.create(shortenUrlRequest.longUrl)

        return ResponseEntity(responseMapper.map(url), HttpStatus.CREATED)
    }
}
