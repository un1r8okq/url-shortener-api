package land.ver.messaging.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "urls")
data class Url(
    @Id
    val id: UUID?,
    val userId: UUID,
    val longUrl: String,
    val shortUrl: String,
    val title: String,
    val description: String,
    val deleted: Boolean,
)
