package land.ver.url.shortener.controllers

import land.ver.url.shortener.dtos.seed.SeedRequest
import land.ver.url.shortener.services.RandomNumberSource
import land.ver.url.shortener.services.RandomStringGenerator
import land.ver.url.shortener.services.ShortUrlCreator
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private const val MAX_SUBDOMAIN_LEN = 12
private const val MAX_PATH_LEN = 64

@RestController
@RequestMapping("/api/v1/seed")
class SeedController(
    private val rndNumSrc: RandomNumberSource,
    private val rndStrGen: RandomStringGenerator,
    private val shortUrlCreator: ShortUrlCreator,
) {
    @PostMapping
    fun seed(@RequestBody request: SeedRequest) {
        val alphabet = ('a'..'z') + ('0'..'9') + ('-')

        repeat(request.urlCount) {
            val subdomain = rndStrGen.generate(alphabet, rndNumSrc.getRandomNumber(1, MAX_SUBDOMAIN_LEN))
            val path = rndStrGen.generate(alphabet, rndNumSrc.getRandomNumber(1, MAX_PATH_LEN))

            shortUrlCreator.create("https://$subdomain.example.com/$path")
        }
    }
}
