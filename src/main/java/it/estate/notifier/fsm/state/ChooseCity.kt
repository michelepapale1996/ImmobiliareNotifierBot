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

class ChooseCity: State {

    override fun handle(request: Message, userProfile: UserProfile, context: FsmContext): Reply {
        if (request.text != null) {
            val cities = context.immobiliareService.searchCity(request.text!!)
            if (cities.isEmpty()) {
                return Reply(userProfile.id, "Non ho trovato alcuna città con quel nome. Dimmi in che città vuoi cercare casa")
            }

            userProfile.foundCities = cities

            if (userProfile.foundCities.size == 1) {
                userProfile.chosenCity = userProfile.foundCities.first()

                if (userProfile.chosenCity!!.zones.isNotEmpty()) {
                    userProfile.state = ChooseZone()
                    return Reply(userProfile.id,"Scegli una delle aree",
                        replyMarkup = KeyboardReplyMarkup(
                            keyboard = generateUsersButtonForZones(userProfile.chosenCity!!.zones),
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
            } else {
                userProfile.state = CityChosen()

                return Reply(userProfile.id, "Scegli una delle città che ho trovato",
                    replyMarkup = KeyboardReplyMarkup(
                        keyboard = generateUsersButtonForCities(cities),
                        resizeKeyboard = true,
                        oneTimeKeyboard = true
                    ))
            }
        }

        return Reply(userProfile.id, "Dimmi in che città vuoi cercare casa")
    }

    private fun generateAdvertiserButtons(): List<List<KeyboardButton>> {
        return listOf(listOf(KeyboardButton("Inserzionista privato")), listOf(KeyboardButton("Sia privati che agenzie")))
    }

    private fun generateUsersButtonForZones(zones: Set<Zone>): List<List<KeyboardButton>> {
        val buttons: MutableList<List<KeyboardButton>> = mutableListOf()
        for (zone in zones) {
            buttons.add(listOf(KeyboardButton(zone.name)))
        }
        return buttons
    }

    private fun generateUsersButtonForCities(cities: Set<City>): List<List<KeyboardButton>> {
        val buttons: MutableList<List<KeyboardButton>> = mutableListOf()
        for (city in cities) {
            buttons.add(listOf(KeyboardButton(city.name)))
        }
        return buttons
    }
}