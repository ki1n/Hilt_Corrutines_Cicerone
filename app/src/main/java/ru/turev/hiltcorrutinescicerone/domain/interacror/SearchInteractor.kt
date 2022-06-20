package ru.turev.hiltcorrutinescicerone.domain.interacror

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchInteractor @Inject constructor() {

    // todo можно и так
    // val searchFlow = MutableStateFlow("")

    val searchFlow =
        MutableSharedFlow<String>(replay = 1, extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND)
}
