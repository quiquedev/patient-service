package info.quiquedev.patientservice.patients

import info.quiquedev.patientservice.patients.usecases.PatientsUseCases
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters.fromValue
import reactor.core.publisher.Mono


@ExtendWith(MockitoExtension::class)
class PatientsRouterTest {
    @Mock
    lateinit var useCases: PatientsUseCases

    @InjectMocks
    lateinit var handler: PatientsHandler

    lateinit var router: PatientsRouter

    @BeforeEach
    fun init() {
        router = PatientsRouter(handler)
    }

    @Test
    fun `create patients should return 400 if body cannot be parsed`() {
        // given
        val body = """{"""
        val client = WebTestClient.bindToRouterFunction(router.createPatient())
            .build()

        // when
        val request = client
            .post()
            .uri { it.path(CREATE_PATIENTS_URI).build() }
            .contentType(APPLICATION_JSON)
            .body(fromValue(body))
            .exchange()

        // then
        request.expectStatus().isBadRequest
    }

    @Test
    fun `create patients should return 400 if body validation fails`() {
        // given
        val invalidBodyJson = """{
            |"name":"${"A".repeat(51)}",
            |"surname":"",
            |"passportNumber":"11"
            |}""".trimMargin()
        val client = WebTestClient.bindToRouterFunction(router.createPatient())
            .build()

        // when
        val request = client
            .post()
            .uri { it.path(CREATE_PATIENTS_URI).build() }
            .contentType(APPLICATION_JSON)
            .body(fromValue(invalidBodyJson))
            .exchange()

        // then
        request.expectStatus().isBadRequest
        request.expectBody().json(
            """{
            |"message":"request body is not valid",
            |"errors":[
            |"'name' length must be between 1 and 50",
            |"'surname' length must be between 1 and 150",
            |"'passportNumber' length must be between 10 and 10"
            |]
            |}""".trimMargin()
        )
    }

    @Test
    fun `create patients should return 400 if passport number already exists`() {
        // given
        val client = WebTestClient.bindToRouterFunction(router.createPatient())
            .build()
        `when`(useCases.createPatient(NEW_PATIENT_DTO)).thenReturn(
            Mono.error(
                ExistingPassportNumberError(
                    NEW_PATIENT_DTO.passportNumber
                )
            )
        )

        // when
        val request = client
            .post()
            .uri { it.path(CREATE_PATIENTS_URI).build() }
            .contentType(APPLICATION_JSON)
            .body(fromValue(NEW_PATIENT_JSON))
            .exchange()

        // then
        request.expectStatus().isBadRequest
    }


    companion object {
        const val CREATE_PATIENTS_URI = "/patients"
        val NEW_PATIENT_DTO = NewPatientDto(
            name = "alim",
            surname = "smith",
            passportNumber = "123456789X"
        )
        val NEW_PATIENT_JSON = """{
            |"name":"${NEW_PATIENT_DTO.name}",
            |"surname":"${NEW_PATIENT_DTO.surname}",
            |"passportNumber":"${NEW_PATIENT_DTO.passportNumber}"
            |}""".trimMargin()
    }

}