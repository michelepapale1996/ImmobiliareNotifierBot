package it.estate.notifier.fsm.state

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import it.estate.notifier.fsm.FsmContext
import it.estate.notifier.fsm.State
import it.estate.notifier.model.UserProfile
import it.estate.notifier.model.City
import it.estate.notifier.model.Zone
import it.estate.notifier.service.request.Reply
import org.bson.codecs.pojo.annotations.BsonDiscriminator

class CityChosen: State {

    override fun handle(request: Message, userProfile: UserProfile, context: FsmContext): Reply {
        if (request.text == null) {
            return Reply(userProfile.id, "Dimmi in che città vuoi cercare casa")
        }

        val chosenCity = retrieveChosenCity(userProfile.foundCities, request.text!!)
        if (chosenCity == null) {
            return Reply(userProfile.id,"Scegli tra una delle opzioni che ti ho dato")
        }

        userProfile.chosenCity = chosenCity

        if (chosenCity.zones.isNotEmpty()) {
            userProfile.state = ChooseZone()
            return Reply(userProfile.id,"Scegli una delle aree",
                replyMarkup = KeyboardReplyMarkup(
                    keyboard = generateUsersButton(chosenCity.zones),
                    resizeKeyboard = true,
                    oneTimeKeyboard = true
                )
            )
        } else {
            userProfile.state = ChooseAdvertiserType()
            return Reply(userProfile.id,
                "Indicami il tipo di inserzionista",
                replyMarkup = KeyboardReplyMarkup(
                    keyboard = generateAdvertiserButtons(),
                    resizeKeyboard = true,
                    oneTimeKeyboard = true
                ))
        }
    }

    private fun generateAdvertiserButtons(): List<List<KeyboardButton>> {
        return listOf(listOf(KeyboardButton("Inserzionista privato")), listOf(KeyboardButton("Sia privati che agenzie")))
    }

    private fun generateUsersButton(zones: Set<Zone>): List<List<KeyboardButton>> {
        val buttons: MutableList<List<KeyboardButton>> = mutableListOf()
        for (zone in zones) {
            buttons.add(listOf(KeyboardButton(zone.name)))
        }
        return buttons
    }

    private fun retrieveChosenCity(cities: Set<City>, cityChosen: String): City? {
        return cities.filter { it.name == cityChosen }.getOrNull(0)
    }
}