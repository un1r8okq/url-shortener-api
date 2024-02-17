package land.ver.messaging

import land.ver.messaging.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun existsByEmailAddress(emailAddress: String): Boolean
}
