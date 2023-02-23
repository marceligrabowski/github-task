package pl.marceligrabowski.githubtask.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.marceligrabowski.githubtask.models.User
import pl.marceligrabowski.githubtask.service.UserService

@RestController
@RequestMapping("/users")
class UsersController(private val userService: UserService) {
    @GetMapping("/{login}")
    fun getUser(@PathVariable login: String): User {
        return userService.getUser(login)
    }
} 