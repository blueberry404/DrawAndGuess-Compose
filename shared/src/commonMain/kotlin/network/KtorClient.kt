package network

import core.exception.DAGException
import core.exception.ErrorResponse
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.serialization.JsonConvertException
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

object KtorClient {
    val client
        get() = HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    explicitNulls = false
                    ignoreUnknownKeys = true
                    expectSuccess = false
                    encodeDefaults = true
                })
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.d(tag = "KTOR", message = message)
                    }
                }
                level = LogLevel.BODY
            }
            HttpResponseValidator {
                validateResponse { response ->
                    when (response.status.value) {
                        in 400..599 -> {
                            val errorResponse = response.body<ErrorResponse>()
                            throw DAGException(errorResponse.error.orEmpty())
                        }
                    }
                }
            }
        }
}

suspend inline fun <reified T> HttpClient.makeRequest(
    block: HttpRequestBuilder.() -> Unit,
): Resource<T> =
    try {
        val response = request { block() }
        Resource.Success(response.body() as T)
    } catch (e: DAGException) {
        Napier.e { e.errorMessage }
        Resource.Error(e.errorMessage)
    } catch (e: IOException) {
        Napier.e { e.message.orEmpty() }
        Resource.Error("Please check your internet connection")
    } catch (e: SerializationException) {
        Napier.e { e.message.orEmpty() }
        Resource.Error(e.message ?: "Serialization error")
    } catch (e: JsonConvertException) {
        Napier.e { e.message.orEmpty() }
        Resource.Error(e.message ?: "Parsing error")
    }