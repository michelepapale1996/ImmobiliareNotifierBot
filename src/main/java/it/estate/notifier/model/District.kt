package it.estate.notifier.model

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty

class District @BsonCreator constructor(@BsonProperty("name") val name: String,
                                        @BsonId val id: Int)