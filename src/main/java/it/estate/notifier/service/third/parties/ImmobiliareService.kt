package it.estate.notifier.service.third.parties

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import it.estate.notifier.model.UserProfile
import it.estate.notifier.model.City
import it.estate.notifier.model.District
import it.estate.notifier.model.Estate
import it.estate.notifier.model.Zone
import org.jboss.logging.Logger
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.ArrayList
import java.util.HashSet
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ImmobiliareService {

    private val logger = Logger.getLogger(ImmobiliareService::class.java)

    fun searchEstate(userProfile: UserProfile): List<Estate> {
        return emptyList()
    }

    fun searchCity(city: String): Set<City> {
        return emptySet()
    }
}