package it.estate.notifier.service.telegram.notifier.update

import io.quarkus.runtime.StartupEvent
import it.estate.notifier.model.UserProfile
import it.estate.notifier.service.db.repository.UserRepository
import javax.enterprise.event.Observes
import javax.inject.Inject

class NotifierServiceBootstrapper {
    @Inject
    lateinit var notifierService: NotifierService

    @Inject
    lateinit var userRepository: UserRepository

    fun onStart(@Observes ev: StartupEvent?) {
        // todo: apply pagination
        val usersFound = userRepository.findAll()
        for (user in usersFound.list()) {
            if (user.notificationsEnabled) {
                notifierService.addToUsersToNotify(user)
            }
        }
    }
}