package info.quiquedev.patientservice.patients.usecases

import org.jooq.impl.DSL
import org.jooq.tools.jdbc.MockConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

val FIXED_CLOCK: Clock = Clock.fixed(
    Instant.ofEpochMilli(1607609508000),
    ZoneId.of("Europe/Berlin")
)

@Configuration
class FixedClockConfig {
    @Bean
    @Primary
    fun fixedClock() = FIXED_CLOCK
}

val TEST_DSL = DSL.using(MockConnection { emptyArray() })