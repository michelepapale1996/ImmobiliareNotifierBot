package it.estate.notifier.fsm

import com.github.kotlintelegrambot.entities.Message
import it.estate.notifier.model.UserProfile
import it.estate.notifier.service.request.Reply
import org.bson.codecs.pojo.annotations.BsonDiscriminator

interface State {
    fun handle(request: Message, userProfile: UserProfile, context: FsmContext): Reply
}