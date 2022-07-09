package it.estate.notifier.fsm.state

import com.github.kotlintelegrambot.entities.Message
import it.estate.notifier.fsm.FsmContext
import it.estate.notifier.fsm.State
import it.estate.notifier.model.UserProfile
import it.estate.notifier.service.request.Reply
import org.bson.codecs.pojo.annotations.BsonDiscriminator

class MinRooms: State {

    override fun handle(request: Message, userProfile: UserProfile, context: FsmContext): Reply {
        if (request.text == null) {
            return Reply(userProfile.id,"Inserisci il minimo numero di locali")
        }

        try {
            userProfile.minRooms = request.text!!.toInt()
            userProfile.state = MaxRooms()

            return Reply(userProfile.id,"Inserisci il massimo numero di locali")
        } catch (e: NumberFormatException) {
            return Reply(userProfile.id,"Numero non valido, inserisci di nuovo il minimo numero di locali")
        }
    }
}