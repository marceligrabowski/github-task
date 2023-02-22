package pl.marceligrabowski.githubtask

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class GithubTaskApplication

fun main(args: Array<String>) {
    runApplication<GithubTaskApplication>(*args)
}
