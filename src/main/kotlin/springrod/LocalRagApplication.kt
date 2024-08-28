package springrod

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LocalRagApplication

fun main(args: Array<String>) {
	runApplication<LocalRagApplication>(*args)
}
