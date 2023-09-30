package di

import core.storage.DefaultKeyValueStorage
import core.storage.KeyValueStorage
import network.DAGRepository
import network.DAGRestService
import org.koin.dsl.module

val commonModule = module {

    single {
        DAGRepository(get(), get())
    }

    single {
        DAGRestService()
    }

    single<KeyValueStorage> {
        DefaultKeyValueStorage()
    }
}