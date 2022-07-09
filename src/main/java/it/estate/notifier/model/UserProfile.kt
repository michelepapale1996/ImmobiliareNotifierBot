package it.estate.notifier.model

import io.quarkus.mongodb.panache.common.MongoEntity
import it.estate.notifier.fsm.State
import it.estate.notifier.fsm.state.UserBootstrap
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonIgnore

@MongoEntity(collection = "user", database = "user")
data class UserProfile(@BsonId var id: Long = 0) {
    var firstName: String? = null
    var lastName: String? = null
    var numberOfNotifications: Int = 0

    var lastNotifiedEstate: Estate? = null

    var forRentOrForSale: Contract = Contract.NOT_CHOSEN
    var notificationsEnabled: Boolean = true
    var chosenCity: City? = null
    var chosenZones: MutableSet<Zone> = mutableSetOf()
    var minPrice: Int? = null
    var maxPrice: Int? = null
    var minRooms: Int? = null
    var maxRooms: Int? = null
    var onlyPrivateAdvertisers: Boolean = false
    var foundCities: Set<City> = HashSet()

    @BsonIgnore
    var state: State = UserBootstrap()

    enum class Contract {
        FOR_RENT, FOR_SALE, NOT_CHOSEN
    }
}