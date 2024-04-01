package land.ver.url.shortener.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.GenericGenerator
import java.time.Clock
import java.time.Instant
import java.util.*

@Entity
@Table(name = "url_visits")
data class UrlVisit(
    @Id
    @GeneratedValue(generator = "uuidv7")
    @GenericGenerator(name = "uuidv7", strategy = "land.ver.url.shortener.Uuidv7Generator")
    val id: UUID?,
    val urlId: UUID?,
    val timestampUtc: Instant,
) {
    constructor() : this(null, null, Instant.now(Clock.systemUTC()))
}
