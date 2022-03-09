package info.quiquedev.patientservice.patients.usecases

import arrow.core.none
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier

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
    lateinit var repository: PatientsRepository

    @Test
    fun `find pating shourd return none if patient cannot be found`() {
        val id = "91ecd50b-b035-46f2-9ba7-8ce99ae33e17"

        StepVerifier
            .create(repository.findPatient(id))
            .expectNext(none())
            .verifyComplete()
    }
}