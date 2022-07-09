package it.estate.notifier.fsm.state

import com.github.kotlintelegrambot.entities.Message
import it.estate.notifier.fsm.FsmContext
import it.estate.notifier.fsm.State
import it.estate.notifier.model.UserProfile
import it.estate.notifier.service.request.Reply

class ChooseAdvertiserType: State {
    override fun handle(request: Message, userProfile: UserProfile, context: FsmContext): Reply {
        if (request.text == null) {
            return Reply(userProfile.id,"Scegli una opzione tra quelle che ti ho dato")
        }

        if (request.text!! == "Inserzionista privato") {
            userProfile.onlyPrivateAdvertisers = true
        } else if (request.text!! == "Sia privati che agenzie") {
            userProfile.onlyPrivateAdvertisers = false
        } else {
            return Reply(userProfile.id,"Scegli una opzione tra quelle che ti ho dato")
        }

        userProfile.state = MinPrice()
        return Reply(userProfile.id,"Indicami il prezzo minimo")
    }
}