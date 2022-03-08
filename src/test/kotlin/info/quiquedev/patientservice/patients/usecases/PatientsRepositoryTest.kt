package info.quiquedev.patientservice.patients.usecases

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    classes = [
        PatientsRepositoryConfig::class,
        FixedClockConfig::class
    ]
)
@EnableAutoConfiguration(
    exclude = [ReactiveOAuth2ClientAutoConfiguration::class]
)
class PatientsRepositoryTest : WithDatabaseContainer {
    @Autowired
    lateinit var patientsRepository: PatientsRepository

    @Test
    fun t() {

    }
}