package info.quiquedev.patientservice.patients.usecases

import arrow.core.none
import arrow.core.some
import info.quiquedev.patientservice.patients.ExistingPassportNumberError
import info.quiquedev.patientsservice.patients.usecases.tables.references.PATIENTS
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

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

    @Autowired
    lateinit var dsl: DSLContext

    @Autowired
    lateinit var clock: Clock

    @BeforeEach
    fun cleanUp() {
        dsl.delete(PATIENTS).execute()
    }

    @Test
    fun `find patient should return none if patient cannot be found`() {
        val id = "91ecd50b-b035-46f2-9ba7-8ce99ae33e17"

        StepVerifier
            .create(repository.findPatient(id))
            .expectNext(none())
            .verifyComplete()
    }

    @Test
    fun `find patient should return some if patient can be found`() {
        val existingPatient = dsl.newRecord(PATIENTS)
        val id = "91ecd50b-b035-46f2-9ba7-8ce99ae33e17"
        existingPatient.id = id
        existingPatient.name = "mohammed"
        existingPatient.surname = "rodriguez"
        existingPatient.passportNumber = "12345687II"
        existingPatient.createdAt = LocalDateTime.ofInstant(Instant.now(clock), ZoneId.of("UTC"))
        existingPatient.store()

        StepVerifier
            .create(repository.findPatient(id))
            .expectNextMatches {
                it == existingPatient.some()
            }
            .verifyComplete()
    }

    @Test
    fun `create patient should raise exception if passport number already exists`() {
        val existingPatient = dsl.newRecord(PATIENTS)
        val id = "91ecd50b-b035-46f2-9ba7-8ce99ae33e17"
        existingPatient.id = id
        existingPatient.name = "mohammed"
        existingPatient.surname = "rodriguez"
        val existingPassportNumber = "12345687II"
        existingPatient.passportNumber = existingPassportNumber
        existingPatient.createdAt = LocalDateTime.ofInstant(Instant.now(clock), ZoneId.of("UTC"))
        existingPatient.store()

        StepVerifier
            .create(
                repository.createPatient(
                    name = "marcel",
                    surname = "lineal",
                    passportNumber = existingPassportNumber
                )
            )
            .expectError(ExistingPassportNumberError::class.java)
            .verify()
    }

    @Test
    fun `create patient create a new patient in the db`() {
        val name = "marcel"
        val surname = "lineal"
        val passportNumber = "12345687II"

        StepVerifier
            .create(
                repository.createPatient(
                    name = name,
                    surname = surname,
                    passportNumber = passportNumber
                )
            )
            .expectNextMatches {
                val patients = dsl.selectFrom(PATIENTS).toList()
                patients.size == 1 && it == patients.first()
            }
            .verifyComplete()
    }
}