package info.quiquedev.patientservice.patients.usecases

import info.quiquedev.patientservice.patients.ExistingPassportNumberError
import info.quiquedev.patientservice.patients.NewPatientDto
import info.quiquedev.patientservice.patients.usecases.PatientsUseCasesTest.TestValues.NEW_PATIENT_DTO
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
class PatientsUseCasesTest {
    @Mock
    lateinit var repository: PatientsRepository

    @InjectMocks
    lateinit var useCases: PatientsUseCases

    @Test
    fun `create patient should raise exception if passport number already exists`() {
        `when`(
            repository.createPatient(
                name = NEW_PATIENT_DTO.name,
                surname = NEW_PATIENT_DTO.surname,
                passportNumber = NEW_PATIENT_DTO.passportNumber
            )
        )
            .thenReturn(
                Mono.error(
                    ExistingPassportNumberError(
                        NEW_PATIENT_DTO.passportNumber
                    )
                )
            )

        StepVerifier
            .create(useCases.createPatient(NEW_PATIENT_DTO))
            .expectError(ExistingPassportNumberError::class.java)
            .verify()
    }

    object TestValues {
        val NEW_PATIENT_DTO = NewPatientDto(
            name = "marcel",
            surname = "lineal",
            passportNumber = "12345687II"
        )
    }

}