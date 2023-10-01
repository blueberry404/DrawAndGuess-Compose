package network

import io.github.aakira.napier.Napier
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import network.response.BaseResponse
import network.Constants.BASE_URL
import network.request.CreateRoomRequest
import network.request.GetUsersInfoRequest
import network.request.JoinRoomRequest
import network.request.UserRequest
import network.response.RemoteRoom
import network.response.RemoteRoomResponse
import network.response.RemoteRoomUser
import network.response.RemoteUser
import network.response.RemoteUserResponse
import network.response.RemoteUsersResponse

class DAGRestService {

    private val client = KtorClient.client

    suspend fun createGuestUser(): Resource<RemoteUser> =
        makeRequest<RemoteUserResponse, RemoteUser>(
            route = USERS_ROUTE,
            method = HttpMethod.Post,
            body = UserRequest()
        )

    suspend fun createRoom(createRoomRequest: CreateRoomRequest): Resource<RemoteRoom> =
        makeRequest<RemoteRoomResponse, RemoteRoom>(
            route = ROOM_ROUTE,
            method = HttpMethod.Post,
            body = createRoomRequest
        )

    suspend fun joinRoom(joinRoomRequest: JoinRoomRequest): Resource<RemoteRoom> =
        makeRequest<RemoteRoomResponse, RemoteRoom>(
            route = ROOM_JOIN_ROUTE,
            method = HttpMethod.Post,
            body = joinRoomRequest
        )

    suspend fun getGameUsers(request: GetUsersInfoRequest): Resource<List<RemoteRoomUser>> =
        makeRequest<RemoteUsersResponse, List<RemoteRoomUser>>(
            route = USERS_INFO_ROUTE,
            method = HttpMethod.Post,
            body = request
        )

    suspend fun getRoom(roomId: String): Resource<RemoteRoom> =
        makeRequest<RemoteRoomResponse, RemoteRoom>(
            route = "$ROOM_ROUTE/$roomId",
            method = HttpMethod.Get,
        )

    private suspend inline fun <reified T : BaseResponse<R>, R> makeRequest(
        route: String,
        method: HttpMethod,
        body: Any? = null
    ): Resource<R> {
        val response = client.makeRequest<T> {
            this.method = method
            url(route)
            contentType(ContentType.Application.Json)
            if (body != null) {
                setBody(body)
            }
        }

        return if (response is Resource.Success) {
            if (response.data.data == null) {
                Resource.Error("No content")
            } else {
                Napier.d { response.data.data.toString() }
                Resource.Success(response.data.data)
            }
        } else {
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