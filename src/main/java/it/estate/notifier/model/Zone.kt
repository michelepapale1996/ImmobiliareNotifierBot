package it.estate.notifier.model

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty

data class Zone @BsonCreator constructor(@BsonProperty("name") val name: String,
                                         @BsonId val id: Int,
                                         @BsonProperty("districts") val districts: Set<District>)