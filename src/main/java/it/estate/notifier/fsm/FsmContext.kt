package it.estate.notifier.fsm

import com.github.kotlintelegrambot.entities.Message
import it.estate.notifier.model.UserProfile
import it.estate.notifier.service.third.parties.ImmobiliareService
import it.estate.notifier.service.request.Reply
import it.estate.notifier.service.telegram.notifier.update.NotifierService
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class FsmContext {

    @Inject
    lateinit var immobiliareService: ImmobiliareService

    @Inject
    lateinit var notifierService: NotifierService

    fun handleMessage(request: Message, userProfile: UserProfile): Reply {
        return userProfile.state.handle(request, userProfile, this)
    }
}