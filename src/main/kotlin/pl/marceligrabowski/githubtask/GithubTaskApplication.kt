package pl.marceligrabowski.githubtask

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GithubTaskApplication

fun main(args: Array<String>) {
    runApplication<GithubTaskApplication>(*args)
}
