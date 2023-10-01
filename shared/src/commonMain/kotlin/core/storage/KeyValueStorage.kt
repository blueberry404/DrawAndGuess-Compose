package core.storage

import models.User

interface KeyValueStorage {

    var user: User?

    fun clear()
}