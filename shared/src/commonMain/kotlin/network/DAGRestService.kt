package network

import io.github.aakira.napier.Napier
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

class DAGRestService {

    private val client = KtorClient.client

    suspend fun createGuestUser(): Resource<RemoteUser> {
        val response = client.makeRequest<RemoteUserResponse> {
            method = HttpMethod.Post
            url(USERS_ROUTE)
            contentType(ContentType.Application.Json)
            setBody(UserRequest())
        }
        return if (response is Resource.Success) {
            if (response.data.data == null) {
                Resource.Error("No content")
            }
            else {
                Napier.d { response.data.data.toString() }
                Resource.Success(response.data.data)
            }
        }
        else {
            response as Resource.Error
        }
    }

    companion object {
        private const val BASE_URL = "http://192.168.1.100:3000/api/v1/"
        private const val USERS_ROUTE = "${BASE_URL}users"
    }
}