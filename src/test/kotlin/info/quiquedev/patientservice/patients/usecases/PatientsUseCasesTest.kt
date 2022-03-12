package info.quiquedev.patientservice.patients.usecases

import info.quiquedev.patientservice.patients.ExistingPassportNumberError
import info.quiquedev.patientservice.patients.NewPatientDto
import info.quiquedev.patientservice.patients.PatientDto
import info.quiquedev.patientservice.patients.usecases.PatientsUseCasesTest.TestValues.NEW_PATIENT_DTO
import info.quiquedev.patientservice.patients.usecases.PatientsUseCasesTest.TestValues.NEW_PATIENT_RECORD
import info.quiquedev.patientservice.patients.usecases.PatientsUseCasesTest.TestValues.PATIENT_DTO
import info.quiquedev.patientsservice.patients.usecases.tables.references.PATIENTS
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Instant
import java.time.Instant.now
import java.time.LocalDateTime
import java.time.ZoneId

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

    @Test
    fun `create patient should create patient`() {
        `when`(
            repository.createPatient(
                name = NEW_PATIENT_DTO.name,
                surname = NEW_PATIENT_DTO.surname,
                passportNumber = NEW_PATIENT_DTO.passportNumber
            )
        )
            .thenReturn(
                Mono.just(NEW_PATIENT_RECORD)
            )

        StepVerifier
            .create(useCases.createPatient(NEW_PATIENT_DTO))
            .expectNext(PATIENT_DTO)
            .verifyComplete()
    }


    object TestValues {
        val NEW_PATIENT_DTO = NewPatientDto(
            name = "marcel",
            surname = "lineal",
            passportNumber = "12345687II"
        )

        val PATIENT_DTO = PatientDto(
            id = "91ecd50b-b035-46f2-9ba7-8ce99ae33e17",
            name = NEW_PATIENT_DTO.name,
            surname = NEW_PATIENT_DTO.surname,
            passportNumber = NEW_PATIENT_DTO.passportNumber,
            createdAt = now(FIXED_CLOCK)
        )

        val NEW_PATIENT_RECORD = let {
            val record = TEST_DSL.newRecord(PATIENTS)
            record.id = "91ecd50b-b035-46f2-9ba7-8ce99ae33e17"
            record.name = PATIENT_DTO.name
            record.surname = PATIENT_DTO.surname
            record.passportNumber = PATIENT_DTO.passportNumber
            record.createdAt = LocalDateTime.ofInstant(PATIENT_DTO.createdAt, ZoneId.of("UTC"))
            record
        }
    }

}