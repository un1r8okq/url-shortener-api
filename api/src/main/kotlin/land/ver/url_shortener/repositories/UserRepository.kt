package land.ver.url_shortener.repositories

import land.ver.url_shortener.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun existsByEmailAddress(emailAddress: String): Boolean
}
