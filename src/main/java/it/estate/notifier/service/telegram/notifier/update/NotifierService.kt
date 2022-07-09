package it.estate.notifier.service.telegram.notifier.update

import it.estate.notifier.model.UserProfile
import it.estate.notifier.model.Estate
import it.estate.notifier.service.third.parties.ImmobiliareService
import it.estate.notifier.service.db.UserService
import javax.enterprise.context.ApplicationScoped
import it.estate.notifier.service.request.Reply
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@ApplicationScoped
class NotifierService {
    private val logger = Logger.getLogger(NotifierService::class.java)

    private val usersToNotify = ConcurrentHashMap<Long, UserProfile>()

    @ConfigProperty(name = "telegram.dev.mode")
    var DEV_MODE_ENABLED: Boolean = true

    @Inject
    lateinit var immobiliareService: ImmobiliareService

    @Inject
    lateinit var userService: UserService

    fun checkAndCreateNotification(): List<Reply> {
        val messages: MutableList<Reply> = ArrayList()
        logger.info("There exist ${usersToNotify.size} that could be notified...")
        val startingInstant = Instant.now()
        logger.info("Starting notification step...")

        var users = usersToNotify.values
        if (DEV_MODE_ENABLED) {
            users = users.filter { it.id == 47672072.toLong() }.toMutableList()
        }

        for (userInfo in users) {
            val notification = createNotificationForUser(userInfo)
            if (notification != null) {
                messages.add(notification)
            }
        }
        logger.info("Ended notification step in ${Duration.between(startingInstant, Instant.now()).toMillis()} ms")
        logger.info("${messages.size} users will be notified!")
        return messages
    }

    private fun createNotificationForUser(userInfo: UserProfile): Reply? {
        if (userInfo.forRentOrForSale == UserProfile.Contract.NOT_CHOSEN) {
            logger.debug("User has not chosen the contract type yet. Skipping it.")
            return null
        }

        if (userInfo.chosenCity == null) {
            logger.debug("User has not chosen the city yet. Skipping it.")
            return null
        }

        val estates = immobiliareService.searchEstate(userInfo)

        val estatesToNotify: List<Estate>
        if (userInfo.lastNotifiedEstate != null) {
            estatesToNotify = filterEstatesToNotify(estates, userInfo.lastNotifiedEstate!!)
        } else {
            estatesToNotify = estates
        }

        if (estatesToNotify.isEmpty()) {
            return null
        }

        userInfo.lastNotifiedEstate = estatesToNotify.getOrNull(0)
        userInfo.numberOfNotifications += estatesToNotify.size

        userService.put(userInfo)

        val text = beautifyMessage(estatesToNotify)
        return Reply(userInfo.id, text)
    }

    private fun filterEstatesToNotify(estates: List<Estate>, lastNotifiedEstate: Estate): List<Estate> {
        val toNotify = mutableListOf<Estate>()
        for (estate in estates) {
            if (estate.url != lastNotifiedEstate.url) {
                toNotify.add(estate)
            } else {
                // since estates is ordered by date, if I've reached an estate already notified, I've to stop
                return toNotify.toList()
            }
        }
        return toNotify.toList()
    }

    private fun beautifyMessage(estates: List<Estate>): String {
        val builder = StringBuilder("Buone notizie! ")
        if (estates.size == 1) {
            builder.append("E' stato caricato un nuovo annuncio:\n")
        } else {
            builder.append("Sono stati caricati ${estates.size} nuovi annunci:\n")
        }
        for (estate in estates) {
            builder.append("""
                
                ${estate.title}
                ${estate.price}
                ${estate.url}
                
            """.trimIndent())
        }

        return builder.toString()
    }

    fun addToUsersToNotify(userProfile: UserProfile) {
        usersToNotify[userProfile.id] = userProfile
    }

    fun removeFromUsersToNotify(userProfile: UserProfile) {
        usersToNotify.remove(userProfile.id)
    }
}