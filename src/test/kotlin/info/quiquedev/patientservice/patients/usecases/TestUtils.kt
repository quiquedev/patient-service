package info.quiquedev.patientservice.patients.usecases

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

val fixedClock: Clock = Clock.fixed(
    Instant.ofEpochMilli(1607609508000),
    ZoneId.of("Europe/Berlin")
)

@Configuration
class FixedClockConfig {
    @Bean
    @Primary
    fun fixedClock() = fixedClock
}