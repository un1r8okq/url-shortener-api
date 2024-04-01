package land.ver.url.shortener.models

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.time.Clock
import java.time.Instant
import java.util.*

@Entity
@Table(name = "urls")
data class Url(
    @Id
    @GeneratedValue(generator = "uuidv7")
    @GenericGenerator(name = "uuidv7", strategy = "land.ver.url.shortener.Uuidv7Generator")
    val id: UUID?,
    val longUrl: String,
    val stub: String,
    val createdTimestampUtc: Instant,
    @OneToMany(mappedBy = "url", fetch = FetchType.EAGER)
    val urlVisits: List<UrlVisit>,
) {
    constructor() : this(null, "", "", Instant.now(Clock.systemUTC()), emptyList())
}
