package core.storage

import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import network.User

class DefaultKeyValueStorage: KeyValueStorage {
    private val settings: Settings by lazy { Settings() }

    override var user: User?
        get() = settings.decodeValueOrNull(User.serializer(), KEY_USER)
        set(value) {
            value?.let {
                settings.encodeValue(User.serializer(), KEY_USER, value)
            } ?: run {
                settings.remove(KEY_USER)
            }
        }

    override fun clear() {
        settings.clear()
    }

    private companion object {
        const val KEY_USER = "KEY_USER"
    }
}