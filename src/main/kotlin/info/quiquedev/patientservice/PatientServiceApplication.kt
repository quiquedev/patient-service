package info.quiquedev.patientservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PatientServiceApplication

fun main(args: Array<String>) {
	runApplication<PatientServiceApplication>(*args)
}
