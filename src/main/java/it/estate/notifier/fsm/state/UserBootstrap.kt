package it.estate.notifier.fsm.state

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import it.estate.notifier.fsm.FsmContext
import it.estate.notifier.fsm.State
import it.estate.notifier.model.UserProfile
import it.estate.notifier.service.request.Reply
import org.bson.codecs.pojo.annotations.BsonDiscriminator

class UserBootstrap: State {
    override fun handle(request: Message, userProfile: UserProfile, context: FsmContext): Reply {
        userProfile.state = ChooseContract()
        return Reply(userProfile.id,
            """
            Per cominciare, dimmi se vuoi cercare casa per acquistarla o affittarla.
            """.trimIndent(),
            replyMarkup = KeyboardReplyMarkup(
                keyboard = generateUserButtons(),
                resizeKeyboard = true,
                oneTimeKeyboard = true
            )
        )
    }

    private fun generateUserButtons(): List<List<KeyboardButton>> {
        return listOf(listOf(KeyboardButton("Acquisto casa")), listOf(KeyboardButton("Affitto casa")))
    }
}