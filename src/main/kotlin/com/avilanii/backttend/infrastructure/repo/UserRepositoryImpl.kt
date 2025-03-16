package com.avilanii.backttend.infrastructure.repo

import com.avilanii.backttend.domain.models.User
import com.avilanii.backttend.domain.repo.UserRepository
import com.avilanii.backttend.infrastructure.database.UserTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryImpl(
    private val database: Database
): UserRepository {
    override suspend fun registerUser(user: User): Int =
        transaction(database) {
            UserTable.insertAndGetId {
                it[name] = user.name
                it[email] = user.email
                it[passwordHash] = user.password
            }.value
        }

    override suspend fun deleteUser(user: User): Boolean =
        transaction(database){
            UserTable.deleteWhere(1){ id.eq(user.id)}==1
        }

    override suspend fun updateUser(user: User): Boolean =
        transaction(database){
            UserTable.update({UserTable.id.eq(user.id)}){
                it[name]= user.name
                it[email]= user.email
                it[passwordHash]= user.password
            } == 1
        }

    override suspend fun findByEmail(email: String): User? =
        transaction(database){
            UserTable.selectAll().where(UserTable.email.eq(email)).map { result ->
                User(
                    id = result[UserTable.id].value,
                    name = result[UserTable.name],
                    email = result[UserTable.email],
                    password = result[UserTable.passwordHash]
                )
            }.singleOrNull()
        }

    override suspend fun findById(id: Int): User? =
        transaction(database){
            UserTable
                .selectAll()
                .where{UserTable.id eq id}
                .map { result ->
                    User(
                        id = id,
                        name = result[UserTable.name],
                        email = result[UserTable.email],
                        password = result[UserTable.passwordHash]
                    )
                }.singleOrNull()
        }
}