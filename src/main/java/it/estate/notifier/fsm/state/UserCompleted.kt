package it.estate.notifier.fsm.state

import com.github.kotlintelegrambot.entities.Message
import it.estate.notifier.fsm.FsmContext
import it.estate.notifier.fsm.State
import it.estate.notifier.model.UserProfile
import it.estate.notifier.service.request.Reply
import org.bson.codecs.pojo.annotations.BsonDiscriminator

class UserCompleted : State {
    override fun handle(request: Message, userProfile: UserProfile, context: FsmContext): Reply {
        userProfile.state = UserCompleted()
        return Reply(userProfile.id, "Finito! Inizierai a ricevere aggiornamenti a breve")
    }
}