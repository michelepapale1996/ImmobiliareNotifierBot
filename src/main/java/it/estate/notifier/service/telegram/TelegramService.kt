package it.estate.notifier.service.telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.payments.PaymentInvoiceInfo
import javax.enterprise.context.ApplicationScoped
import it.estate.notifier.service.telegram.notifier.update.NotifierService
import javax.enterprise.event.Observes
import io.quarkus.runtime.StartupEvent
import io.quarkus.scheduler.Scheduled
import io.quarkus.runtime.ShutdownEvent
import it.estate.notifier.service.request.RequestHandler
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import javax.inject.Inject

@ApplicationScoped
class TelegramService {
    private val logger = Logger.getLogger(TelegramService::class.java)
    private lateinit var bot: Bot

    @ConfigProperty(name = "telegram.authorization.token")
    lateinit var BOT_TOKEN: String

    @ConfigProperty(name = "telegram.dev.mode")
    var DEV_MODE_ENABLED: Boolean = true

    @Inject
    lateinit var notifierService: NotifierService

    @Inject
    lateinit var requestHandler: RequestHandler

    fun onStart(@Observes ev: StartupEvent?) {
        bot = bot {
            token = BOT_TOKEN
            dispatch {
                text {
                    try {
                        val reply = requestHandler.process(message)
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = reply.text,
                            replyMarkup = reply.replyMarkup
                        )
                        // bot.sendInvoice(ChatId.fromId(message.chat.id), PaymentInvoiceInfo())
                    } catch (ex: Exception) {
                        logger.error(ex)
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id), text = "Non sono riuscito a processare la richiesta...riprova!"
                        )
                    }
                }
            }
        }
        bot.startPolling()
    }

    @Scheduled(cron = "{telegram.notifier.cron.scheduled.time}")
    fun checkAndSendNotification() {
        logger.info("Checking for notifications...")
        val notifications = notifierService.checkAndCreateNotification()
        for (notification in notifications) {
            logger.debug("Sending notification $notification")
            if (!DEV_MODE_ENABLED) {
                bot.sendMessage(ChatId.fromId(notification.chatId), notification.text, replyMarkup = notification.replyMarkup)
            } else {
                logger.info("Dev mode enabled. I've not sent notification to user.")
            }
        }
    }

    fun onStop(@Observes ev: ShutdownEvent?) {
        bot.stopPolling()
        logger.info("Telegram bot shut down gracefully.")
    }
}