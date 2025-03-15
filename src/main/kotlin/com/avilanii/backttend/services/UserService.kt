package com.avilanii.backttend.services

import com.avilanii.backttend.infrastructure.repo.UserRepositoryImpl

class UserService(
    private val userRepository: UserRepositoryImpl
) {
}