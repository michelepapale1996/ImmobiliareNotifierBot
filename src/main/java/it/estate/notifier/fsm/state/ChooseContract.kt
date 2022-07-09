package it.estate.notifier.fsm.state

import com.github.kotlintelegrambot.entities.Message
import it.estate.notifier.fsm.FsmContext
import it.estate.notifier.fsm.State
import it.estate.notifier.model.UserProfile
import it.estate.notifier.service.request.Reply

class ChooseContract: State {
    override fun handle(request: Message, userProfile: UserProfile, context: FsmContext): Reply {
        if (request.text == null) {
            return Reply(userProfile.id,"Scegli una opzione tra quelle che ti ho dato")
        }

        if (request.text!! == "Acquisto casa") {
            userProfile.forRentOrForSale = UserProfile.Contract.FOR_SALE
        } else if (request.text!! == "Affitto casa") {
            userProfile.forRentOrForSale = UserProfile.Contract.FOR_RENT
        } else {
            return Reply(userProfile.id,"Scegli una opzione tra quelle che ti ho dato")
        }

        userProfile.state = ChooseCity()
        return Reply(userProfile.id,"Dimmi in che città vuoi cercare casa")
    }
}