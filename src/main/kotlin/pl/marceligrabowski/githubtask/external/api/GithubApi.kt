package pl.marceligrabowski.githubtask.external.api

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import pl.marceligrabowski.githubtask.external.models.GithubUser

//TODO: consider move to webflux + webclient/ktor - reactive approach
@FeignClient(
    value = "github",
    url = "\${github.api.url}"
)
interface GithubApi {
    @RequestMapping(method = [RequestMethod.GET], value = ["/users/{login}"])
    fun getUser(@PathVariable("login") login: String): GithubUser
}