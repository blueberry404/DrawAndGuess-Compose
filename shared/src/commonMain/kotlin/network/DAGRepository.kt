package network

import core.storage.DefaultKeyValueStorage
import createroom.RoomStatus
import createroom.RoomStatus.Unknown
import home.GameMode
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

    suspend fun createRoom(name: String, password: String, mode: String): Resource<Room> {
        val user = keyValueStorage.user
        val result: Resource<Room> = if (user == null) {
            Error("Some error occurred. Try reopening the app with internet on")
        } else {
            val createRoomRequest = CreateRoomRequest(user.id, name, password, mode)
            val response = service.createRoom(createRoomRequest)
            Napier.d { "Room response:: $response" }
            if (response is Resource.Success) {
                val roomStatus = mapRoom(response.data)
                Success(roomStatus)
            } else {
                Error((response as Error).error)
            }
        }
        return result
    }

    suspend fun joinRoom(name: String, password: String): Resource<Room> {
        val user = keyValueStorage.user
        val result = if (user == null) {
            Error("Some error occurred. Try reopening the app with internet on")
        } else {
            val joinRoomRequest = JoinRoomRequest(user.id, name, password)
            val response = service.joinRoom(joinRoomRequest)
            Napier.d { "Room response:: $response" }
            if (response is Resource.Success) {
                val roomStatus = mapRoom(response.data)
                Success(roomStatus)
            } else {
                Error((response as Error).error)
            }
        }
        return result
    }

    private fun mapRoom(remoteRoom: RemoteRoom): Room {
        val roomStatus = when (remoteRoom.status) {
            "Created" -> RoomStatus.Created
            "Ready" -> RoomStatus.Ready
            "GameStarted" -> RoomStatus.GameStarted
            "Finished" -> RoomStatus.Finished
            else -> Unknown
        }
        val gameMode = when (remoteRoom.mode) {
            "Single" -> GameMode.Single
            else -> GameMode.Many
        }
        val users = remoteRoom.users.map {
            RoomUser(it.id, it.username, it.avatarColor)
        }
        return with(remoteRoom) {
            Room(id, gameMode, gameRounds, roomStatus, users, userTurns)
        }
    }
}