package core.storage

import network.User

interface KeyValueStorage {

    var user: User?

    fun clear()
}