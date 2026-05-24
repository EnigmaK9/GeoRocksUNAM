// File path: app/src/main/java/com/enigma/georocks/data/RockRepository.kt

package com.enigma.georocks.data

import com.enigma.georocks.data.db.FavoriteRockDao
import com.enigma.georocks.data.db.FavoriteRockEntity
import com.enigma.georocks.data.remote.api.RockApiService
import com.enigma.georocks.data.remote.model.RockDetailDto
import com.enigma.georocks.data.remote.model.RockDto
import com.enigma.georocks.data.remote.model.SampleResponseDto
import com.enigma.georocks.data.remote.model.TokenResponseDto
import com.enigma.georocks.data.remote.model.UserCreateRequestDto
import com.enigma.georocks.data.remote.model.UserResponseDto
import com.enigma.georocks.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RockRepository(
    private val apiService: RockApiService,
    private val favoriteRockDao: FavoriteRockDao
) {

    // Fetch rock details from the API using suspend function
    suspend fun getRockDetail(id: String): RockDetailDto = withContext(Dispatchers.IO) {
        apiService.getRockDetail(id).toRockDetailDto()
    }

    // Fetch list of rocks from the API using suspend function
    suspend fun getRocksApiary(): MutableList<RockDto> = withContext(Dispatchers.IO) {
        apiService.getRocks().map { it.toRockDto() }.toMutableList()
    }

    // Authenticate with FastAPI backend
    suspend fun login(username: String, password: String): TokenResponseDto = withContext(Dispatchers.IO) {
        apiService.login(username, password)
    }

    // Register a new user in FastAPI backend
    suspend fun signup(
        username: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): UserResponseDto = withContext(Dispatchers.IO) {
        val request = UserCreateRequestDto(username, email, password, firstName, lastName)
        apiService.signup(request)
    }

    // Get current authenticated user profile
    suspend fun getMe(): UserResponseDto = withContext(Dispatchers.IO) {
        apiService.getMe()
    }

    // Helper conversion from SampleResponseDto to RockDto
    private fun SampleResponseDto.toRockDto(): RockDto {
        val imageUrl = if (!picture.isNullOrBlank() && picture != "Sin muestra") {
            if (picture.startsWith("http")) picture else "${Constants.IMAGE_BASE_URL}$picture"
        } else {
            null
        }
        return RockDto(
            id = uid,
            thumbnail = imageUrl,
            title = rockName
        )
    }

    // Helper conversion from SampleResponseDto to RockDetailDto
    private fun SampleResponseDto.toRockDetailDto(): RockDetailDto {
        val imageUrl = if (!picture.isNullOrBlank() && picture != "Sin muestra") {
            if (picture.startsWith("http")) picture else "${Constants.IMAGE_BASE_URL}$picture"
        } else {
            null
        }

        val state = "Corte: ${if (cut) "Sí" else "No"} | Lámina: ${if (thinSection) "Sí" else "No"}"
        val location = "Localidad: $locationName, $locationCountry"

        val generatedDesc = if (!rockDescription.isNullOrBlank()) {
            rockDescription
        } else {
            "Muestra geológica catalogada de tipo **$rockName**, descubierta y recolectada en la localidad de **$locationName**, ubicada en **$locationCountry**.\n\n" +
            "**Detalles del Espécimen:**\n" +
            "• **Corte de Exhibición:** ${if (cut) "Sí, preparado para visualización macroscópica de texturas." else "Muestra en su estado natural."}\n" +
            "• **Estudio en Lámina Delgada:** ${if (thinSection) "Sí, cuenta con lámina delgada preparada para análisis petrológico bajo microscopio polarizado." else "No disponible para análisis microscópico."}"
        }

        return RockDetailDto(
            title = rockName,
            image = imageUrl,
            video = null,
            longDesc = generatedDesc,
            aMemberOf = state,
            alsoKnownAs = emptyList(),
            formula = null,
            hardness = null,
            color = location,
            magnetic = null,
            healthRisks = null,
            latitude = null,
            longitude = null,
            images = imageUrl?.let { listOf(it) } ?: emptyList(),
            localities = listOf(locationName),
            frequentlyAskedQuestions = emptyList()
        )
    }

    // Add a rock to favorites using RockDto
    suspend fun addToFavorites(rockDto: RockDto) = withContext(Dispatchers.IO) {
        val entity = FavoriteRockEntity(
            rockId = rockDto.id,
            title = rockDto.title,
            thumbnail = rockDto.thumbnail
        )
        favoriteRockDao.insertFavorite(entity)
    }

    // Remove a rock from favorites using RockDto
    suspend fun removeFromFavorites(rockDto: RockDto) = withContext(Dispatchers.IO) {
        val entity = FavoriteRockEntity(
            rockId = rockDto.id,
            title = null,        // Title can be set to null or retained based on preference
            thumbnail = null     // Thumbnail can be set to null or retained based on preference
        )
        favoriteRockDao.deleteFavorite(entity)
    }

    // Check if a rock is favorited
    suspend fun isRockFavorited(rockId: String): Boolean = withContext(Dispatchers.IO) {
        favoriteRockDao.getFavoriteById(rockId) != null
    }

    // Retrieve all favorite rocks
    suspend fun getAllFavoriteRocks(): List<FavoriteRockEntity> = withContext(Dispatchers.IO) {
        favoriteRockDao.getAllFavorites()
    }
}
