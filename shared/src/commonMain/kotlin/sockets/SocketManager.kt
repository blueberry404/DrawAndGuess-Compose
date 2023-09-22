package sockets

import core.GlobalData
import core.storage.DefaultKeyValueStorage
import createroom.RoomContentMode
import createroom.RoomContentMode.Create
import io.github.aakira.napier.Napier
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import network.Constants.SOCKET_URL
import network.User
import sockets.SocketEvent.PrepareForGame
import sockets.SocketEvent.RoomInfo
import sockets.SocketEvent.StartGame
import sockets.SocketEvent.UserJoined
import sockets.SocketEvent.UserLeft

object SocketManager: PlatformSocketListener {

    private val platformSocket = PlatformSocket(SOCKET_URL)
    private var listener: SocketEventsListener? = null

    private var userIds: List<String> = emptyList()
    private val keyValueStorage = DefaultKeyValueStorage()
    private var user: User? = null

    const val KEY_PREPARE_GAME = "PrepareGame"
    const val KEY_START_GAME = "StartGame"

    init {
        user = keyValueStorage.user
    }

    fun setListener(listener: SocketEventsListener) {
        this.listener = listener
    }

    fun removeListener() {
        this.listener = null
    }

    fun connect() {
        platformSocket.connectSocket(this)
    }

    fun addDefaultRoomUser(userId: String) {
        userIds = listOf(userId)
    }

    fun getRoomUserIds() = userIds

    fun enterUserToRoom(roomContentMode: RoomContentMode) {
        val roomId = GlobalData.room.id
        val payload = MessagePayload(
            userId = user?.id.orEmpty(),
            roomId = roomId
        )
        val type = if (roomContentMode == Create) "create" else "join"
        val message = SocketMessage(type, payload)
        val json = Json.encodeToString(message)
        Napier.d { "Socket:: join user:: $json" }
        platformSocket.sendMessage(json)
    }

    fun requestRoomInfo() {
        val roomId = GlobalData.room.id
        val payload = MessagePayload(roomId = roomId)
        val message = SocketMessage("info", payload)
        val json = Json.encodeToString(message)
        Napier.d { "Socket:: requestRoomInfo:: $json" }
        platformSocket.sendMessage(json)
    }

    fun signalForGame(type: String) {
        val roomId = GlobalData.room.id
        val payload = MessagePayload(
            userId = user?.id.orEmpty(),
            roomId = roomId,
        )
        val message = SocketMessage(type, payload)
        val json = Json.encodeToString(message)
        Napier.d { "Sending signal:: $json" }
        platformSocket.sendMessage(json)
    }

    override fun onOpen() {
        Napier.d { "Connected!!!" }
        listener?.onConnected()
    }

    override fun onFailure(t: Throwable) {
        Napier.e { "Failure:: ${t.message}" }
        listener?.onFailure(t.message ?: "Some error occurred")
    }

    override fun onMessage(msg: String) {
        Napier.d { "Received a message!!! :: $msg" }
        processMessage(msg)
    }

    override fun onClosing(code: Int, reason: String) {
        Napier.d { "OnClosing::: code: $code, reason: $reason" }
    }

    override fun onClosed(code: Int, reason: String) {
        Napier.e { "Closed::: code: $code, reason: $reason" }
        listener?.onDisconnected()
    }

    private fun processMessage(message: String) {
        try {
            val json = Json {
                isLenient = true
                ignoreUnknownKeys = true
            }

            val socketMessage = json.decodeFromString<SocketMessage>(message)
            when (socketMessage.type) {
                "Join" -> {
                    socketMessage.payload.userIds?.let {
                        Napier.d { "listeber: $listener" }
                        userIds = it.toList()
                        listener?.onEvent(UserJoined(it))
                    }
                }
                "Leave" -> {
                    socketMessage.payload.userIds?.let {
                        userIds = it.toList()
                        listener?.onEvent(UserLeft(it))
                    }
                }
                "Info" -> {
                    socketMessage.payload.userIds?.let {
                        userIds = it.toList()
                        listener?.onEvent(RoomInfo(it))
                    }
                }
                "PrepareForGame" -> listener?.onEvent(PrepareForGame)
                "StartGame" -> listener?.onEvent(StartGame)
            }
        } catch (exception: SerializationException) {
            Napier.e { exception.message.orEmpty() }
        } catch (exception: IllegalArgumentException) {
            Napier.e { exception.message.orEmpty() }
        }
    }
}