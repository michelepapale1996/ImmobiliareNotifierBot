package it.estate.notifier.model

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty

data class City @BsonCreator constructor(@BsonProperty("name") val name: String,
                                         @BsonId val id: Int,
                                         @BsonProperty("zones") val zones: Set<Zone>)