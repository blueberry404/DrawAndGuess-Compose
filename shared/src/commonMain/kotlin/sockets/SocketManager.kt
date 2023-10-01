package sockets

import core.GlobalData
import core.storage.DefaultKeyValueStorage
import models.RoomContentMode
import models.RoomContentMode.Create
import ui.game.models.CanvasState
import io.github.aakira.napier.Napier
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import network.Constants.SOCKET_URL
import models.User
import sockets.SocketEvent.NewRound
import sockets.SocketEvent.PrepareForGame
import sockets.SocketEvent.RoomInfo
import sockets.SocketEvent.RoundOver
import sockets.SocketEvent.StartGame
import sockets.SocketEvent.SyncDrawing
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
    const val KEY_END_GAME = "EndGame"
    const val KEY_CORRECT_GUESS = "CorrectGuess"
    const val KEY_WRONG_GUESS = "WrongGuess"
    const val KEY_NEW_ROUND = "NewRound"

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

    fun syncCanvas(state: CanvasState) {
        val payload = MessagePayload(
            userId = user?.id.orEmpty(),
            roomId = GlobalData.room.id,
            canvasState = state,
        )
        val message = SocketMessage("Sync", payload)
        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
        val jsonString = json.encodeToString(message)
        platformSocket.sendMessage(jsonString)
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
                encodeDefaults = true
            }

            val socketMessage = json.decodeFromString<SocketMessage>(message)
            when (socketMessage.type) {
                "Join" -> {
                    socketMessage.payload.userIds?.let {
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
                "Sync" -> {
                    socketMessage.payload.canvasState?.let {
                        listener?.onEvent(SyncDrawing(it))
                    }
                }
                "RoundOver" -> {
                    val payload = socketMessage.payload
                    listener?.onEvent(RoundOver(payload.wonRound, payload.userId))
                }
                "NewRound" -> listener?.onEvent(NewRound)
            }
        } catch (exception: SerializationException) {
            Napier.e { exception.message.orEmpty() }
        } catch (exception: IllegalArgumentException) {
            Napier.e { exception.message.orEmpty() }
        }
    }
}