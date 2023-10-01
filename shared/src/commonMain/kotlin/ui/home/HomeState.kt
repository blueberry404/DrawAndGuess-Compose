package ui.home

import models.AvatarInfo

data class HomeState(val isLoading: Boolean = false, val errorMessage: String? = null, val user: HomeUserInfo? = null) {
    fun hasError() = errorMessage.isNullOrEmpty().not()
}

data class HomeUserInfo(val welcomeText: String, val avatarInfo: AvatarInfo)