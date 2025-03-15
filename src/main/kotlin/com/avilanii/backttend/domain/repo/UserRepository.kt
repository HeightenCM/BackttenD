package com.avilanii.backttend.domain.repo

import com.avilanii.backttend.domain.models.User

interface UserRepository {
    suspend fun registerUser(user: User): Int
    suspend fun findByEmailAndPassword(email: String, password: String): User?
    suspend fun deleteUser(user: User): Boolean
    suspend fun updateUser(user: User): Boolean
}