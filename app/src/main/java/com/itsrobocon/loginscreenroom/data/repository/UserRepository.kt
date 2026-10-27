package com.itsrobocon.loginscreenroom.data.repository

import com.itsrobocon.loginscreenroom.data.local.UserDao
import com.itsrobocon.loginscreenroom.data.local.UserEntity
import com.itsrobocon.loginscreenroom.util.PasswordHasher

sealed class AuthResult {
    data class Success(val message: String, val username: String? = null) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(username: String, password: String): AuthResult {
        val trimmedUsername = username.trim()
        if (trimmedUsername.isBlank() || password.isBlank()) {
            return AuthResult.Error("Username and password cannot be empty")
        }

        val existingUser = userDao.getUserByUsername(trimmedUsername)
        if (existingUser != null) {
            return AuthResult.Error("Username already exists")
        }

        val passwordHash = PasswordHasher.hashPassword(password)
        val user = UserEntity(username = trimmedUsername, passwordHash = passwordHash)

        return try {
            userDao.insertUser(user)
            AuthResult.Success("Registration successful! You can now log in.")
        } catch (e: Exception) {
            AuthResult.Error("Registration failed: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun loginUser(username: String, password: String): AuthResult {
        val trimmedUsername = username.trim()
        if (trimmedUsername.isBlank() || password.isBlank()) {
            return AuthResult.Error("Username and password cannot be empty")
        }

        val user = userDao.getUserByUsername(trimmedUsername)
            ?: return AuthResult.Error("Invalid username or password")

        val passwordHash = PasswordHasher.hashPassword(password)
        return if (user.passwordHash == passwordHash) {
            AuthResult.Success("Login successful!", username = user.username)
        } else {
            AuthResult.Error("Invalid username or password")
        }
    }
}
