package it.estate.notifier.fsm.state

import com.github.kotlintelegrambot.entities.Message
import it.estate.notifier.fsm.FsmContext
import it.estate.notifier.fsm.State
import it.estate.notifier.model.UserProfile
import it.estate.notifier.service.request.Reply
import org.bson.codecs.pojo.annotations.BsonDiscriminator

class MaxRooms: State {

    override fun handle(request: Message, userProfile: UserProfile, context: FsmContext): Reply {
        if (request.text == null) {
            return Reply(userProfile.id,"Inserisci il massimo numero di locali")
        }

        try {
            userProfile.maxRooms = request.text!!.toInt()
            userProfile.state = UserCompleted()

            context.notifierService.addToUsersToNotify(userProfile)
            return Reply(userProfile.id,"Finito! Riceverai una notifica ogni volta che verrà pubblicato un nuovo annuncio!")
        } catch (e: NumberFormatException) {
            return Reply(userProfile.id,"Numero non valido, inserisci di nuovo il massimo numero di locali")
        }
    }
}