package land.ver.url.shortener.services

import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class RandomNumberService {
    fun getRandomNumber(from: Int, until: Int) = Random.nextInt(from, until)
}
