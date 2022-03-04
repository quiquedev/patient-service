package info.quiquedev.patientservicekotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PatientServiceKotlinApplication

fun main(args: Array<String>) {
	runApplication<PatientServiceKotlinApplication>(*args)
}
