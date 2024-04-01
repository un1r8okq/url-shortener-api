package land.ver.url.shortener.controllers

import jakarta.validation.Valid
import land.ver.url.shortener.API_PAGE_SIZE
import land.ver.url.shortener.dtos.PagedApiResult
import land.ver.url.shortener.dtos.PaginationMetadata
import land.ver.url.shortener.dtos.urls.ShortenUrlRequest
import land.ver.url.shortener.dtos.urls.UrlResponse
import land.ver.url.shortener.exceptions.InvalidPageNumberException
import land.ver.url.shortener.mappers.UrlResponseMapper
import land.ver.url.shortener.models.Url
import land.ver.url.shortener.repositories.UrlRepository
import land.ver.url.shortener.services.StubGeneratorService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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
        if (pageNumber < 0) {
            throw InvalidPageNumberException(pageNumber)
        }

        val pageable = Pageable.ofSize(API_PAGE_SIZE).withPage(pageNumber)
        val pagedResult = urlRepository.findAll(pageable)
        val urls = pagedResult.toList().map { UrlResponseMapper().map(it, Instant.now()) }

        return PagedApiResult(urls, PaginationMetadata(pagedResult.number, pagedResult.totalPages, pagedResult.size))
    }

    @PostMapping("")
    fun create(@Valid @RequestBody shortenUrlRequest: ShortenUrlRequest): ResponseEntity<UrlResponse> {
        val url = urlRepository.save(
            Url(
                id = null,
                longUrl = shortenUrlRequest.longUrl,
                stub = stubGenerator.generate(),
                createdTimestampUtc = Instant.now(Clock.systemUTC())
            )
        )

        return ResponseEntity(UrlResponseMapper().map(url, Instant.now()), HttpStatus.CREATED)
    }
}
