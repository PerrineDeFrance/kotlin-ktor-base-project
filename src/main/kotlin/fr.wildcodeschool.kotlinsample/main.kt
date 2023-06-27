package fr.wildcodeschool.kotlinsample

import kotlinx.coroutines.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import io.ktor.client.statement.*
import io.ktor.content.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import java.io.File


@Serializable
data class RocketLaunch(
    @SerialName("flight_number")
    val flightNumber: Int,
    @SerialName("name")
    val missionName: String,
    @SerialName("date_utc")
    val launchDateUTC: String,
    @SerialName("details")
    val details: String?,
    @SerialName("success")
    val launchSuccess: Boolean?,
    @SerialName("links")
    val links: Links
) {
}

@Serializable
data class Links(
    @SerialName("patch")
    val patch: Patch?,
    @SerialName("article")
    val article: String?
)

@Serializable
data class Patch(
    @SerialName("small")
    val small: String?,
    @SerialName("large")
    val large: String?
)

class SpaceXApi {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            })
        }
    }

    suspend fun getAllLaunches(): List<RocketLaunch> {
        return httpClient.get("https://api.spacexdata.com/v5/launches").body()
    }

    suspend fun downloadArticle(articleUrl: String, destinationFolder: String) {
    val client = HttpClient()
    try {
        val response: HttpResponse = client.get(articleUrl)
        val fileName = articleUrl.substringAfterLast("/")
        val time=System.currentTimeMillis()
        val file = File("./bin/${time}.html")
        
        val urlArt=Url(articleUrl)
        response.bodyAsChannel().copyAndClose(file.writeChannel())



    println("répertoire courant : " + File(".").getAbsolutePath())
        println("downloaded : $fileName")
    } catch (e: Exception) {
        println("download fail : $articleUrl")
        e.printStackTrace()
    } 
}
}

fun main() = runBlocking<Unit> {
    val service = SpaceXApi()
    val launches: List<RocketLaunch> = service.getAllLaunches()

    var i = 0
    for (l in launches) {
        println("Launch $i : ${l}")
        i++
    }

    val destinationFolder = "articles"
    

coroutineScope {
        launches.forEach { launch ->
            launch.links.article?.let { articleUrl ->
                async {
                    service.downloadArticle(articleUrl, destinationFolder)
}}

      
            }}}

