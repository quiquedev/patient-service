package info.quiquedev.patientservice.patients

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.ParallelFlux

// TODO: remove unused methods when service is fully implemented
object ReactorUtils {
    fun <T> Mono<T>.execute(mono: Mono<Unit>): Mono<T> =
        flatMap { value ->
            mono.map { value }
        }

    fun <T> Mono<T>.execute(callable: (T) -> Unit): Mono<T> =
        flatMap { input ->
            execute(callable, input).map { input }
        }

    private fun <I, O> execute(callable: (I) -> O, element: I): Mono<O> =
        safeMono { callable(element) }

    fun <T> Flux<T>.execute(callable: (T) -> Unit): Flux<T> =
        flatMap { input ->
            execute(callable, input).map { input }
        }

    fun <T> ParallelFlux<T>.execute(callable: (T) -> Unit): ParallelFlux<T> =
        flatMap { input ->
            execute(callable, input).map { input }
        }

    fun <T> safeMono(callable: () -> T) =
        Mono.defer {
            try {
                callable.invoke()?.let { Mono.just(it) } ?: throw NullPointerException()
            } catch (t: Throwable) {
                Mono.error(t)
            }
        }
}
