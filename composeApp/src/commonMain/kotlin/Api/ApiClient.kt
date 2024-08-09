package Api



import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.InternalAPI
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.lighthousegames.logging.logging
import viewmodel.HistoryModel
import viewmodel.checkdata
import viewmodel.endtripmodel
import viewmodel.loginmodel
import viewmodel.message
import viewmodel.profilemodel
import viewmodel.trailer_model


class ApiClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.BODY
        }
    }
    @Serializable
    data class PrecheckItem(
        val item_name: String,
        val status: String
    )
    @OptIn(InternalAPI::class)
    suspend fun sendlocation(driverId: String, lat:String, lonString: String):message?{
        return  withContext(Dispatchers.Default){
            try {
                val formData = MultiPartFormDataContent(
                    formData {
                        append("driver_id", driverId)
                        append("longitude", lonString)
                        append("latitude", lat)
                    }
                )
                val response:HttpResponse=client.post(Api.Location.url){
                    method = HttpMethod.Post
                    contentType(ContentType.MultiPart.FormData)
                    body = formData
                }
                Json.decodeFromString<message>(response.bodyAsText())

                    }catch (e:Exception){

                val log = logging("sendTrailerData1")
                log.d { e.toString() }
                e.printStackTrace()

                null
            }
        }


    }
    @OptIn(InternalAPI::class)
suspend fun getweekhistoru(driverId: String):HistoryModel?
{
return  withContext(Dispatchers.Default){
    try {
        val response:HttpResponse=client.get(Api.History.url+"?driver_id=$driverId")
        {
            method=HttpMethod.Get
        }
        Json.decodeFromString<HistoryModel>(response.bodyAsText())
    }
    catch (e:Exception){
        val log = logging("sendTrailerData1")
        log.d { e.toString() }
        e.printStackTrace()

        null
    }
}
}
    @OptIn(InternalAPI::class)
suspend fun postTrailerCheckData(
    trailerModel: trailer_model
):message?{
return  withContext(Dispatchers.Default){
    try {
        val jsonPayload = Json.encodeToString(trailerModel)
val response:HttpResponse=client.post(Api.Trailer_precheck.url){
    method= HttpMethod.Post
    contentType(ContentType.Application.Json)
    body=jsonPayload

}
        Json.decodeFromString<message>(response.bodyAsText())

    }
    catch (e:Exception){
        val log = logging("sendTrailerData1")
        log.d { e.toString() }
        e.printStackTrace()

        null
    }
}
}

    @OptIn(InternalAPI::class)
    suspend fun postPrecheckData(
        driverId: String,
        meterReading: String,
        date: String,
        precheckItems: List<PrecheckItem>,
        imageUpload: ByteArray? = null,
        remark: String
    ): message? {

        return withContext(Dispatchers.Default) {
            try {


                val formData = MultiPartFormDataContent(
                    formData {
                        append("driver_id", driverId)
                        append("meter_reading", meterReading)
                        append("date", date)
                        append("remark",remark)

                        precheckItems.forEachIndexed { index, item ->
                            append("precheck_items[$index][item_name]", item.item_name)
                            append("precheck_items[$index][status]", item.status)
                        }

                        imageUpload?.let {
                            append("image_upload", it, Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=image.jpg")
                            })
                        }
                    }
                )

                val response: HttpResponse = client.post(Api.Truck_precheck.url) {
                    method = HttpMethod.Post
                    contentType(ContentType.MultiPart.FormData)
                    body = formData
                }

                Json{ignoreUnknownKeys=true}.decodeFromString<message>(response.bodyAsText())

            } catch (e: Exception) {
                val log = logging("sendTruckData1")
                log.d { e.toString() }
                e.printStackTrace()

                null
            }
        }
    }
    suspend fun profile(driverId: String):profilemodel?{
        return withContext(Dispatchers.Default){
            try {
                val response:HttpResponse=client.get(Api.Profile.url+"?driver_id=$driverId"){
                    method=HttpMethod.Get
                }
                val responseBody = response.bodyAsText()
                println("Response Body: $responseBody")

                Json { ignoreUnknownKeys = true }.decodeFromString<profilemodel>(responseBody)
            }
            catch (e:Exception){
                val log = logging("sendTruckDataIOException")
                log.d { e.toString() }
                e.printStackTrace()
                null
            }
        }
    }
    @OptIn(InternalAPI::class)
    suspend fun endtrip(
        driverId: String,
        meterReading: String,
        imageUpload: ByteArray? = null
    ): endtripmodel? {
        return withContext(Dispatchers.Default) {
            try {
                val formData = MultiPartFormDataContent(
                    formData {
                        append("driver_id", driverId)
                        append("meter_reading", meterReading)
                        imageUpload?.let {
                            append("image_upload", it, Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=image.jpg")
                            })
                        }
                    }
                )


                val response: HttpResponse = client.post(Api.EndTrip.url) {
                    method = HttpMethod.Post
                    contentType(ContentType.MultiPart.FormData)
                    body = formData
                }

                // Log the response for debugging
                val responseBody = response.bodyAsText()
                println("Response Body: $responseBody")

                Json { ignoreUnknownKeys = true }.decodeFromString<endtripmodel>(responseBody)
            } catch (e: IOException) {
                // Log and handle IOException separately
                val log = logging("sendTruckDataIOException")
                log.d { e.toString() }
                e.printStackTrace()
                null
            } catch (e: Exception) {
                val log = logging("sendTruckDataException")
                log.d { e.toString() }
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun loginCall(url: String, parameters: Map<String, String>): loginmodel {
      try {
          val response: HttpResponse = client.post(url) {
              headers {
                  append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
              }
              setBody(parameters.entries.joinToString("&") { "${it.key}=${it.value}" })
          }

          val json = """
        {
            "driver": {
                "id": "123",
                "name": "John Doe",
                "licenseNumber": "ABC123"
            },
            "message": "Login successful",
            "status": "success"
        }
    """
          val jsonResponse = response.bodyAsText()
          val loginModel = Json.decodeFromString<loginmodel>(jsonResponse)
          return loginModel

      }
      finally {
          client.close()
      }


    }
    suspend fun getPrecheckStatus(driverId: String): checkdata? {
        return withContext(Dispatchers.Default) {
            try {
                val response: HttpResponse = client.get(Api.CheckData.url + "?driver_id=$driverId") {
                    method = HttpMethod.Get
                }
                Json{ignoreUnknownKeys=true}.decodeFromString<checkdata>(response.bodyAsText())
            } catch (e: Exception) {
                val log = logging("sendTrailerData1")
                log.d { e.toString() }
                e.printStackTrace()
                null
            }
        }
    }
    }



