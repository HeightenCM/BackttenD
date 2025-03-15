package com.avilanii.backttend.services

import com.avilanii.backttend.domain.models.User
import com.avilanii.backttend.infrastructure.repo.UserRepositoryImpl
import com.avilanii.backttend.services.middleware.checkPassword
import com.avilanii.backttend.services.middleware.hashPassword
import com.avilanii.backttend.services.middleware.verifyPassword

class UserService(
    private val userRepository: UserRepositoryImpl
) {
    suspend fun registerUser(name: String, email: String, password: String): Int{
        val hashedPassword = hashPassword(password)
        val user = User(
            name = name,
            email = email,
            password = hashedPassword
        )
        return userRepository.registerUser(user)
    }

    suspend fun login(email: String, password: String): User?{
        val user = userRepository.findByEmail(email) ?: return null
        return if(verifyPassword(password, user.password))
            user
        else null
    }

    suspend fun deleteUser(user: User, password: String): Boolean{
        return if (checkPassword(user, password))
            userRepository.deleteUser(user)
        else false
    }

    suspend fun updateUser(user: User): Boolean{
        return userRepository.updateUser(user)
    }
}