package land.ver.url_shortener

import com.github.f4b6a3.uuid.UuidCreator
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import java.util.*

class Uuidv7Generator : IdentifierGenerator {
    override fun generate(session: SharedSessionContractImplementor, p2: Any): UUID
        = UuidCreator.getTimeOrderedEpoch()
}