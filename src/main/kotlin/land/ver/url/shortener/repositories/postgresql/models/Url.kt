package land.ver.url.shortener.repositories.postgresql.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "urls")
data class Url(
    @Id
    val id: UUID,
    val longUrl: String,
    val stub: String,
    val createdTimestampUtc: Instant,
)
