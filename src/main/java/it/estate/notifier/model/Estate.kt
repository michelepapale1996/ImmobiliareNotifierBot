package it.estate.notifier.model

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty


data class Estate @BsonCreator constructor(@BsonProperty("title") val title: String,
                                           @BsonProperty("price") val price: String,
                                           @BsonProperty("url") val url: String,
                                           @BsonProperty("privateAdvertiser") val privateAdvertiser: Boolean)