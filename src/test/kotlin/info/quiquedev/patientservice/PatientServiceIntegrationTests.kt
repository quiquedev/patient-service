package info.quiquedev.patientservice

import info.quiquedev.patientservice.patients.usecases.FIXED_CLOCK
import info.quiquedev.patientservice.patients.usecases.WithDatabaseContainer
import info.quiquedev.patientsservice.patients.usecases.tables.references.PATIENTS
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.equalTo
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters.fromValue
import java.time.Clock
import java.time.Instant.now
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

@SpringBootTest(webEnvironment = RANDOM_PORT)
class PatientServiceIntegrationTests : WithDatabaseContainer {
    @Value("\${local.server.port}")
    var serverPort: Int = 0

    lateinit var client: WebTestClient

    @Autowired
    lateinit var dsl: DSLContext


    @Autowired
    lateinit var clock: Clock

    @BeforeEach
    fun prepareTest() {
        client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$serverPort")
            .build()
        dsl.delete(PATIENTS).execute()
    }

    @Test
    fun `patient is created`() {
        // given
        assertThat(dsl.selectFrom(PATIENTS).toList()).isEmpty()

        // when
        val response = client
            .post()
            .uri { it.path(PATIENTS_URI).build() }
            .contentType(APPLICATION_JSON)
            .body(fromValue(NEW_PATIENT_JSON))
            .exchange()

        // then


        //// new patient is stored in db
        val patients = dsl.selectFrom(PATIENTS).toList()
        assertThat(patients).size().isEqualTo(1)
        val patient = patients.first()

        UUID.fromString(patient.id)
        assertThat(patient.name).isEqualTo(NAME)
        assertThat(patient.surname).isEqualTo(SURNAME)
        assertThat(patient.passportNumber).isEqualTo(PASSPORT_NUMBER)
        assertThat(patient.createdAt).isEqualTo(CREATED_AT)

        //// new patient is returned
        response.expectStatus().isCreated
        response.expectBody()
            .jsonPath("$.id")
            .value(equalTo(patient.id))
            .jsonPath("$.name")
            .value(equalTo(patient.name))
            .jsonPath("$.surname")
            .value(equalTo(patient.surname))
            .jsonPath("$.passportNumber")
            .value(equalTo(patient.passportNumber))
            .jsonPath("$.createdAt")
            .value(equalTo(now(clock).toString()))
    }

    @Test
    fun `patient cannot be created because passport number is not unique`() {
        // given
        val patientRecord = dsl.newRecord(PATIENTS)
        patientRecord.id = "373e48f0-f864-4f01-a45d-b81ff653662a"
        patientRecord.name = NAME
        patientRecord.surname = SURNAME
        patientRecord.passportNumber = PASSPORT_NUMBER
        patientRecord.createdAt = CREATED_AT
        patientRecord.store()

        // when
        val response = client
            .post()
            .uri { it.path(PATIENTS_URI).build() }
            .contentType(APPLICATION_JSON)
            .body(fromValue(NEW_PATIENT_JSON))
            .exchange()

        // then
        response.expectStatus().isBadRequest
        response.expectBody().json("""{"message": "existing passport number '$PASSPORT_NUMBER'"}""")
    }

    private companion object {
        const val PATIENTS_URI = "/patients"
        const val NAME = "mola"
        const val SURNAME = "siebert"
        const val PASSPORT_NUMBER = "987678910T"
        val CREATED_AT: LocalDateTime = LocalDateTime.ofInstant(now(FIXED_CLOCK), ZoneId.of("UTC"))

        val NEW_PATIENT_JSON = """{
            |"name":"$NAME",
            |"surname":"$SURNAME",
            |"passportNumber":"$PASSPORT_NUMBER"
            |}""".trimMargin()
    }

}

