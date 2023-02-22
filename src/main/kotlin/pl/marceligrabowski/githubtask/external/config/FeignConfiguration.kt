package pl.marceligrabowski.githubtask.external.config

import feign.codec.ErrorDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.marceligrabowski.githubtask.external.error.GithubApiErrorDecoder

@Configuration
class FeignConfiguration {
    @Bean
    fun errorDecoder(): ErrorDecoder = GithubApiErrorDecoder()
}