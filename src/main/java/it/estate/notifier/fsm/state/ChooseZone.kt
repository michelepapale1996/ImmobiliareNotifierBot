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
import java.lang.StringBuilder

class ChooseZone: State {

    override fun handle(request: Message, userProfile: UserProfile, context: FsmContext): Reply {
        if (request.text == null) {
            return Reply(userProfile.id, "Scegli una delle aree")
        }

        if (userProfile.chosenCity == null) {
            userProfile.state = ChooseCity()
            return Reply(userProfile.id,"Dimmi in che città vuoi cercare casa")
        }

        if (request.text == "Fatto" || request.text == "fatto") {
            userProfile.state = ChooseAdvertiserType()
            return Reply(userProfile.id,
                "Indicami il tipo di inserzionista",
                    replyMarkup = KeyboardReplyMarkup(
                            keyboard = generateAdvertiserButtons(),
                            resizeKeyboard = true,
                            oneTimeKeyboard = true
                        ))
        }

        val chosenZoneWithoutEmoji = request.text!!.replace("❌ ", "").replace("✅ ", "")

        val chosenZone = retrieveChosenZone(userProfile.chosenCity!!, chosenZoneWithoutEmoji)
        if (chosenZone == null) {
            return Reply(userProfile.id,"Scegli tra una delle opzioni")
        }

        if (userProfile.chosenZones.contains(chosenZone)) {
            userProfile.chosenZones.remove(chosenZone)
        } else {
            userProfile.chosenZones.add(chosenZone)
        }

        val beautifiedZones: String = beautifyZones(userProfile.chosenZones)
        return Reply(userProfile.id,
"""
Hai correttamente aggiunto le seguenti aree:
$beautifiedZones

Seleziona un'altra area, oppure digita 'Fatto' per continuare
        """.trimMargin(),
            replyMarkup = KeyboardReplyMarkup(
                keyboard = generateUsersButton(userProfile.chosenCity!!.zones, userProfile.chosenZones),
                resizeKeyboard = true,
                oneTimeKeyboard = true
            )
        )
    }

    private fun generateAdvertiserButtons(): List<List<KeyboardButton>> {
        return listOf(listOf(KeyboardButton("Inserzionista privato")), listOf(KeyboardButton("Sia privati che agenzie")))
    }

    private fun beautifyZones(chosenZones: MutableSet<Zone>): String {
        val builder = StringBuilder()
        for (zone in chosenZones) {
            builder.append("✅ ${zone.name}\n")
        }
        return builder.toString()
    }

    private fun generateUsersButton(availableZonesInChosenCity: Set<Zone>, zonesAlreadyChosen: Set<Zone>): List<List<KeyboardButton>> {
        val buttons: MutableList<List<KeyboardButton>> = mutableListOf()
        for (zone in availableZonesInChosenCity) {
            var nameToRender = "${zone.name}"
            if (zonesAlreadyChosen.contains(zone)) {
                nameToRender = "✅ ${zone.name}"
            }

            buttons.add(listOf(KeyboardButton(nameToRender)))
        }
        return buttons
    }

    private fun retrieveChosenZone(city: City, chosenZone: String): Zone? {
        return city.zones.filter { it.name == chosenZone }.getOrNull(0)
    }
}