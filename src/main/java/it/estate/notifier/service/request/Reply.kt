package it.estate.notifier.service.request

import com.github.kotlintelegrambot.entities.ReplyMarkup

data class Reply (val chatId: Long, val text: String, val replyMarkup: ReplyMarkup? = null)