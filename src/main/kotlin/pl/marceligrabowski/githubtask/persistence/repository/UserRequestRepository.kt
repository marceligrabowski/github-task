package pl.marceligrabowski.githubtask.persistence.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import pl.marceligrabowski.githubtask.persistence.models.UserRequest

@Repository
interface UserRequestRepository : CrudRepository<UserRequest, String> {
}