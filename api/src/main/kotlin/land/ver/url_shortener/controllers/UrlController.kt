package land.ver.url_shortener.controllers

import land.ver.url_shortener.API_PAGE_SIZE
import land.ver.url_shortener.StubGeneratorService
import land.ver.url_shortener.UrlRepository
import land.ver.url_shortener.dtos.ShortenUrlRequest
import land.ver.url_shortener.dtos.UrlResponse
import land.ver.url_shortener.exceptions.InvalidPageNumberException
import land.ver.url_shortener.http.PagedApiResult
import land.ver.url_shortener.http.PaginationMetadata
import land.ver.url_shortener.models.Url
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
        val urls = pagedResult.toList().map {
            UrlResponse(
                longUrl = it.longUrl,
                shortenedUrl = getShortUrlPrefix() + it.stub
            )
        }

        return PagedApiResult(urls, PaginationMetadata(pagedResult.number, pagedResult.totalPages, pagedResult.size))
    }

    @PostMapping("")
    fun create(@RequestBody shortenUrlRequest: ShortenUrlRequest): ResponseEntity<UrlResponse> {
        val url = urlRepository.save(Url(
            id = null,
            longUrl = shortenUrlRequest.longUrl,
            stub = stubGenerator.generate(),
        ))

        val response = UrlResponse(
            longUrl = url.longUrl,
            shortenedUrl = getShortUrlPrefix() + url.stub,
        )

        return ResponseEntity(response, HttpStatus.CREATED)
    }

    private fun getShortUrlPrefix() = "/s/";
}
