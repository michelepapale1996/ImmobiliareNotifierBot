package it.estate.notifier.fsm.state

import com.github.kotlintelegrambot.entities.Message
import it.estate.notifier.fsm.FsmContext
import it.estate.notifier.fsm.State
import it.estate.notifier.model.UserProfile
import it.estate.notifier.service.request.Reply
import org.bson.codecs.pojo.annotations.BsonDiscriminator

class MaxPrice: State {

    override fun handle(request: Message, userProfile: UserProfile, context: FsmContext): Reply {
        if (request.text == null) {
            return Reply(userProfile.id,"Inserisci il prezzo massimo")
        }

        try {
            userProfile.maxPrice = request.text!!.toInt()
            userProfile.state = MinRooms()

            return Reply(userProfile.id,"Inserisci il minimo numero di locali")
        } catch (e: NumberFormatException) {
            return Reply(userProfile.id,"Numero non valido, inserisci di nuovo il prezzo massimo")
        }
    }
}