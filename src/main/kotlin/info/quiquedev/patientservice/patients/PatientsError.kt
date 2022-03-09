package info.quiquedev.patientservice.patients

sealed class PatientsError(message: String, throwable: Throwable? = null) : Throwable(message, throwable)
data class ExistingPassportNumberError(val passportNumber: String) :
    PatientsError("existing passport number $passportNumber")

data class UnexpectedError(val throwable: Throwable) :
    PatientsError("unexpected error", throwable)

data class TooManyPatientsError(val id: String, val amount: Int) :
    PatientsError("$amount patients found for id $id")