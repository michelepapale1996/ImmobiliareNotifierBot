package it.estate.notifier.service.db.repository

import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepositoryBase
import it.estate.notifier.model.UserProfile
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserRepository: PanacheMongoRepositoryBase<UserProfile, Long>