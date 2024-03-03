package land.ver.url_shortener.controllers

import jakarta.validation.Valid
import land.ver.url_shortener.API_PAGE_SIZE
import land.ver.url_shortener.services.StubGeneratorService
import land.ver.url_shortener.repositories.UrlRepository
import land.ver.url_shortener.dtos.urls.ShortenUrlRequest
import land.ver.url_shortener.dtos.urls.UrlResponse
import land.ver.url_shortener.exceptions.InvalidPageNumberException
import land.ver.url_shortener.dtos.PagedApiResult
import land.ver.url_shortener.dtos.PaginationMetadata
import land.ver.url_shortener.mappers.UrlResponseMapper
import land.ver.url_shortener.models.Url
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Clock
import java.time.Instant

@RestController
@RequestMapping("/api/v1/urls")
class UrlController(
    private val urlRepository: UrlRepository,
    private val stubGenerator: StubGeneratorService,
) {
    @GetMapping("", "/")
    fun index(@RequestParam pageNumber: Int): PagedApiResult<UrlResponse> {
        if (pageNumber < 1) {
            throw InvalidPageNumberException(pageNumber)
        }

        val pageable = Pageable.ofSize(API_PAGE_SIZE).withPage(pageNumber - 1)
        val pagedResult = urlRepository.findAll(pageable)
        val urls = pagedResult.toList().map { UrlResponseMapper().map(it) }

        return PagedApiResult(urls, PaginationMetadata(pagedResult.number + 1, pagedResult.totalPages, pagedResult.size))
    }

    @PostMapping("")
    fun create(@Valid @RequestBody shortenUrlRequest: ShortenUrlRequest): ResponseEntity<UrlResponse> {
        val url = urlRepository.save(Url(
            id = null,
            longUrl = shortenUrlRequest.longUrl,
            stub = stubGenerator.generate(),
            createdTimestampUtc = Instant.now(Clock.systemUTC())
        ))

        return ResponseEntity(UrlResponseMapper().map(url), HttpStatus.CREATED)
    }
}
