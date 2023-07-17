package network

import core.storage.DefaultKeyValueStorage
import io.github.aakira.napier.Napier
import network.Resource.Error
import network.Resource.Success

class DAGRepository {

    private val service = DAGRestService()
    private val keyValueStorage = DefaultKeyValueStorage()

    suspend fun getUser(): Resource<User> {
        val user = keyValueStorage.user
        Napier.d { "User not found" }
        val resource: Resource<User> = if (user == null) {
            val response = service.createGuestUser()
            if (response is Success) {
                val remoteUser = response.data
                val newUser = User(remoteUser.id, remoteUser.username, remoteUser.avatarColor)
                keyValueStorage.user = newUser
                Napier.d { "Saved user $newUser" }
                Success(newUser)
            } else
                response as Error
        } else {
            Napier.d { "Found user:: $user" }
            Success(user)
        }
        return resource
    }
}