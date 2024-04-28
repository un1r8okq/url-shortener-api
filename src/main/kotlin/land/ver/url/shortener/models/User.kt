package land.ver.url.shortener.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import org.hibernate.annotations.GenericGenerator
import java.util.*

@Entity
data class User(
    @Id
    @GenericGenerator(name = "uuidv7", strategy = "land.ver.url.shortener.Uuidv7Generator")
    var id: UUID? = null,

    @field:Email
    @field:NotEmpty
    @Column(name = "email")
    val emailAddress: String = "",

    @field:NotEmpty
    val name: String = "",
)
