package com.avilanii.backttend.services.middleware

import com.avilanii.backttend.domain.models.User
import org.mindrot.jbcrypt.BCrypt

fun hashPassword(password: String): String{
    return BCrypt.hashpw(password, BCrypt.gensalt())
}

fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
    return BCrypt.checkpw(plainPassword, hashedPassword)
}

fun checkPassword(user: User, password: String): Boolean{
    val hashedPassword = hashPassword(password)
    return verifyPassword(hashedPassword, user.password)
}