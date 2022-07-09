package it.estate.notifier.service.db

import it.estate.notifier.model.UserProfile
import it.estate.notifier.service.db.repository.UserRepository
import java.util.concurrent.ConcurrentHashMap
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class UserService {

    @Inject
    lateinit var userRepository: UserRepository

    private val cache = ConcurrentHashMap<Long, UserProfile>()

    fun put(userInfo: UserProfile) {
        cache[userInfo.id] = userInfo
        userRepository.persistOrUpdate(userInfo)
    }

    fun remove(chatId: Long) {
        cache.remove(chatId)
        userRepository.deleteById(chatId)
    }

    fun get(chatId: Long): UserProfile? {
        if (cache[chatId] != null) return cache[chatId]
        val userPreferences = retrieveFromDB(chatId)
        if (userPreferences != null) {
            put(userPreferences)
        }
        return userPreferences
    }

    private fun retrieveFromDB(chatId: Long): UserProfile? {
        return userRepository.findById(chatId)
    }
}