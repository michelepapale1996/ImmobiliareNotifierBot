package it.estate.notifier.service.request

import com.github.kotlintelegrambot.entities.Message
import it.estate.notifier.fsm.FsmContext
import it.estate.notifier.model.UserProfile
import it.estate.notifier.service.db.UserService
import it.estate.notifier.service.telegram.notifier.update.NotifierService
import org.jboss.logging.Logger
import java.lang.StringBuilder
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class RequestHandler {
    private val logger = Logger.getLogger(RequestHandler::class.java)

    @Inject
    lateinit var userService: UserService

    @Inject
    lateinit var notifierService: NotifierService

    @Inject
    lateinit var fsmContext: FsmContext

    fun process(request: Message): Reply {
        val userPreferences = retrieveUser(request)

        val reply: Reply
        if (request.text == "/help") {
            return helpHandler(userPreferences)
        } else if (request.text == "/start") {
            return startHandler(userPreferences)
        } else if (request.text == "/startnotifications") {
            return startNotificationsHandler(userPreferences)
        } else if (request.text == "/stopnotifications") {
            return stopNotficationsHandler(userPreferences)
        } else if (request.text == "/preferences") {
            return preferencesHandler(userPreferences)
        } else if (request.text == "/editpreferences") {
            return editPreferencesHandler(request, userPreferences)
        } else {
            reply = fsmContext.handleMessage(request, userPreferences)
            userService.put(userPreferences)
            return reply
        }
    }

    /* --------------- private utils --------------- */

    private fun retrieveUser(request: Message): UserProfile {
        var userPreferences = userService.get(request.chat.id)

        if (userPreferences == null) {
            userPreferences = UserProfile(request.chat.id)
            if (request.chat.firstName != null) {
                userPreferences.firstName = request.chat.firstName
            }
            if (request.chat.lastName != null) {
                userPreferences.lastName = request.chat.lastName
            }
        }

        return userPreferences
    }

    private fun editPreferencesHandler(request: Message, userProfile: UserProfile): Reply {
        notifierService.removeFromUsersToNotify(userProfile)

        var userPreferences = userProfile
        userPreferences = UserProfile(request.chat.id)
        if (request.chat.firstName != null) {
            userPreferences.firstName = request.chat.firstName
        }
        if (request.chat.lastName != null) {
            userPreferences.lastName = request.chat.lastName
        }

        val reply = fsmContext.handleMessage(request, userPreferences)
        userService.put(userPreferences)

        return reply
    }

    private fun preferencesHandler(userProfile: UserProfile): Reply {
        val responseBuilder = StringBuilder()

        var contractType = ""
        var notificationsEnabled = ""
        if (userProfile.forRentOrForSale == UserProfile.Contract.NOT_CHOSEN) {
            contractType = "Non hai ancora scelto se ricevere annunci per case in vendita o in affito"
        } else {
            if (userProfile.forRentOrForSale == UserProfile.Contract.FOR_SALE) {
                contractType = "Hai scelto di ricevere annunci per case in vendita"
            } else if (userProfile.forRentOrForSale == UserProfile.Contract.FOR_RENT) {
                contractType = "Hai scelto di ricevere annunci per case in affitto"
            }

            notificationsEnabled = "Hai deciso di abilitare le notifiche"
            if (!userProfile.notificationsEnabled) {
                notificationsEnabled = "Hai deciso di disabilitare le notifiche"
            }
        }

        var onlyPrivateAdvertisers = "Hai deciso di ricevere aggiornamenti sia da privati che da agenzie"
        if (userProfile.onlyPrivateAdvertisers) {
            onlyPrivateAdvertisers = "Hai deciso di ricevere aggiornamenti per inserzioni solo da privati"
        }

        var chosenCity = ""
        if (userProfile.chosenCity != null) {
            chosenCity = "Hai scelto come città: ${userProfile.chosenCity!!.name}"
        }

        var chosenZones = ""
        if (userProfile.chosenZones.isNotEmpty()) {
            chosenZones = "Hai deciso di ricevere aggiornamenti per questi quartieri:\n"
            for (zone in userProfile.chosenZones) {
                chosenZones += "- ${zone.name}\n"
            }
        }

        responseBuilder.append(
"""
Ciao ${userProfile.firstName}! Queste sono le tue preferenze:

$contractType
$notificationsEnabled
$onlyPrivateAdvertisers
$chosenCity
$chosenZones
""".trimMargin()
        )

        return Reply(userProfile.id, responseBuilder.toString())
    }

    private fun startHandler(userProfile: UserProfile): Reply {
        logger.info("A new user (${userProfile.id}) has started the bot!")
        return Reply(userProfile.id, "Ciao! Questo è ImmobiliareNotifierBot. Sono in grado di inviarti notifiche ogni volta che viene inserito un annuncio di tuo interesse su Immobiliare.it. In questo modo, non dovrai entrare ogni giorno su Immobiliare.it per vedere se ci sono nuovi annunci riguardanti nuove case! Per cominciare, digita /editpreferences")
    }

    private fun stopNotficationsHandler(userProfile: UserProfile): Reply {
        userProfile.notificationsEnabled = false

        userService.put(userProfile)

        notifierService.removeFromUsersToNotify(userProfile)
        logger.info("User (${userProfile.id}) has decided to stop notifications!")
        return Reply(userProfile.id, "D'ora in poi non riceverai più aggiornamenti")
    }

    private fun startNotificationsHandler(userProfile: UserProfile): Reply {
        if (userCanReceiveUpdates(userProfile)) {
            userProfile.notificationsEnabled = true

            userService.put(userProfile)

            notifierService.addToUsersToNotify(userProfile)

            logger.info("User (${userProfile.id}) has decided to start notifications!")
            return Reply(userProfile.id, "D'ora in poi riceverai aggiornamenti per nuove inserzioni")
        } else {
            return Reply(userProfile.id, "Per iniziare a ricevere aggiornamenti, digita /editpreferences")
        }
    }

    private fun helpHandler(userProfile: UserProfile): Reply {
        val text = """
                Available commands:
                /help Per ricevere informazioni dei comandi disponibili
                /start Mostra un messaggio di benvenuto
                /startnotifications Per iniziare a ricevere aggiornamenti di nuovi annunci
                /stopnotifications Per smettere di ricevere ulteriori aggiornamenti
                /editpreferences Per aggiornare le tue preferenze di ricerca
                /preferences Per mostrare le tue attuali preferenze di ricerca
            """.trimIndent()
        return Reply(userProfile.id, text)
    }

    private fun userCanReceiveUpdates(userProfile: UserProfile): Boolean {
        return userProfile.chosenCity != null
    }
}