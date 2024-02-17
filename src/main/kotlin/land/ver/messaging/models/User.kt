package land.ver.messaging.models

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import org.hibernate.annotations.GenericGenerator
import java.util.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(generator = "uuidv7")
    @GenericGenerator(name = "uuidv7", strategy = "land.ver.messaging.Uuidv7Generator")
    var id: UUID? = null,

    @field:Email
    @field:NotEmpty
    @Column(name = "email")
    val emailAddress: String = "",

    @field:NotEmpty
    val name: String = "",
)
