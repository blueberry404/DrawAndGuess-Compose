package network

import io.github.aakira.napier.Napier
import network.Resource.Error
import network.Resource.Success

class DAGRepository {

    private val service = DAGRestService()

    suspend fun getUser(): Resource<User> {
        val response = service.createGuestUser()
        return if (response is Success) {
            val remoteUser = response.data
            val user = User(remoteUser.id, remoteUser.username, remoteUser.avatarColor)
            Success(user)
        }
        else
            response as Error
    }
}