package network

import io.github.aakira.napier.Napier
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import network.Constants.BASE_URL

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

    suspend fun createRoom(createRoomRequest: CreateRoomRequest): Resource<RemoteRoom> {
        val response = client.makeRequest<RemoteRoomResponse> {
            method = HttpMethod.Post
            url(ROOM_ROUTE)
            contentType(ContentType.Application.Json)
            setBody(createRoomRequest)
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

    suspend fun joinRoom(joinRoomRequest: JoinRoomRequest): Resource<RemoteRoom> {
        val response = client.makeRequest<RemoteRoomResponse> {
            method = HttpMethod.Post
            url(ROOM_JOIN_ROUTE)
            contentType(ContentType.Application.Json)
            setBody(joinRoomRequest)
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

    suspend fun getGameUsers(request: GetUsersInfoRequest): Resource<List<RemoteRoomUser>> {
        val response = client.makeRequest<RemoteUsersResponse> {
            method = HttpMethod.Post
            url(USERS_INFO_ROUTE)
            contentType(ContentType.Application.Json)
            setBody(request)
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
        private const val USERS_ROUTE = "${BASE_URL}users"
        private const val USERS_INFO_ROUTE = "${BASE_URL}users/info"
        private const val ROOM_ROUTE = "${BASE_URL}room"
        private const val ROOM_JOIN_ROUTE = "${BASE_URL}room/join"
    }
}