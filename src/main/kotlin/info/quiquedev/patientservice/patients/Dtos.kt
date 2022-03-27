package info.quiquedev.patientservice.patients

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import org.hibernate.validator.constraints.Length
import java.time.Instant

data class NewPatientDto(
    @get:Length(
        min = 1,
        max = 50,
        message = "'name' length must be between {min} and {max}"
    )
    val name: String,
    @get:Length(
        min = 1,
        max = 150,
        message = "'surname' length must be between {min} and {max}"
    )
    val surname: String,
    @get:Length(
        min = 10,
        max = 10,
        message = "'passportNumber' length must be between {min} and {max}"
    )
    val passportNumber: String
)

data class PatientDto(
    val id: String,
    val name: String,
    val surname: String,
    val passportNumber: String,
    @JsonFormat(shape = STRING)
    val createdAt: Instant
)

data class RestErrorDto(
    val message: String,

    @JsonInclude(NON_NULL)
    val errors: Set<String>? = null
)