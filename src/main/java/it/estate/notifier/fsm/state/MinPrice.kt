package it.estate.notifier.fsm.state

import com.github.kotlintelegrambot.entities.Message
import it.estate.notifier.fsm.FsmContext
import it.estate.notifier.fsm.State
import it.estate.notifier.model.UserProfile
import it.estate.notifier.service.request.Reply
import org.bson.codecs.pojo.annotations.BsonDiscriminator

class MinPrice: State {

    override fun handle(request: Message, userProfile: UserProfile, context: FsmContext): Reply {
        if (request.text == null) {
            return Reply(userProfile.id,"Inserisci il prezzo minimo")
        }

        try {
            userProfile.minPrice = request.text!!.toInt()
            userProfile.state = MaxPrice()

            return Reply(userProfile.id,"Inserisci il prezzo massimo")
        } catch (e: NumberFormatException) {
            return Reply(userProfile.id,"Numero non valido, inserisci di nuovo il prezzo minimo")
        }
    }
}