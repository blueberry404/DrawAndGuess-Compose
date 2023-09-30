package di

import org.koin.core.context.startKoin

fun doInitKoin(){
    startKoin {
        modules(commonModule)
    }
}