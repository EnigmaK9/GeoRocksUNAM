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

    private var malformedUid: String? = null

    // Fetch rock details from the API using suspend function
    suspend fun getRockDetail(id: String): RockDetailDto = withContext(Dispatchers.IO) {
        if (id == malformedUid) {
            getCustomRockDetail(21)
        } else if (id.contains("-22")) {
            getCustomRockDetail(22)
        } else if (id.contains("-23")) {
            getCustomRockDetail(23)
        } else if (id.contains("-24")) {
            getCustomRockDetail(24)
        } else if (id.contains("-21")) {
            getCustomRockDetail(21)
        } else {
            try {
                val detailDto = apiService.getRockDetail(id)
                if (detailDto.rockName.contains("\n") || detailDto.rockName.contains("Pitchstone")) {
                    malformedUid = id
                    getCustomRockDetail(21)
                } else {
                    detailDto.toRockDetailDto()
                }
            } catch (e: Exception) {
                val suffixIndex = id.lastIndexOf("-")
                if (suffixIndex != -1) {
                    val suffix = id.substring(suffixIndex + 1)
                    if (suffix == "22" || suffix == "23" || suffix == "24" || suffix == "21") {
                        getCustomRockDetail(suffix.toInt())
                    } else {
                        throw e
                    }
                } else {
                    throw e
                }
            }
        }
    }

    // Fetch list of rocks from the API using suspend function
    suspend fun getRocksApiary(): MutableList<RockDto> = withContext(Dispatchers.IO) {
        val originalList = apiService.getRocks()
        val processedList = mutableListOf<RockDto>()
        for (sample in originalList) {
            if (sample.rockName.contains("\n") || sample.rockName.contains("Pitchstone")) {
                malformedUid = sample.uid
                processedList.addAll(parseMalformedSampleList(sample))
            } else {
                processedList.add(sample.toRockDto())
            }
        }
        processedList
    }

    private fun parseMalformedSampleList(sample: SampleResponseDto): List<RockDto> {
        malformedUid = sample.uid
        return listOf(
            RockDto(
                id = "${sample.uid}-21",
                thumbnail = "${Constants.IMAGE_BASE_URL}pitchstone_piedra_pez.jpg",
                title = "Pitchstone “Piedra pez”"
            ),
            RockDto(
                id = "${sample.uid}-22",
                thumbnail = "${Constants.IMAGE_BASE_URL}pitchstone_porfirítica.jpg",
                title = "Pitchstone porfirítica"
            ),
            RockDto(
                id = "${sample.uid}-23",
                thumbnail = "${Constants.IMAGE_BASE_URL}nordmarquita.jpg",
                title = "Nordmarquita"
            ),
            RockDto(
                id = "${sample.uid}-24",
                thumbnail = "${Constants.IMAGE_BASE_URL}larvikita_laurvigita.jpg",
                title = "Larvikita, laurvigita"
            )
        )
    }

    private fun getCustomRockDetail(index: Int): RockDetailDto {
        val title: String
        val imgName: String
        val locName: String
        val country: String

        when (index) {
            22 -> {
                title = "Pitchstone porfirítica"
                imgName = "pitchstone_porfirítica.jpg"
                locName = "Ardnamurchan"
                country = "Oeste de Escocia"
            }
            23 -> {
                title = "Nordmarquita"
                imgName = "nordmarquita.jpg"
                locName = "Sutherlandshire"
                country = "Escocia"
            }
            24 -> {
                title = "Larvikita, laurvigita"
                imgName = "larvikita_laurvigita.jpg"
                locName = "Larvik"
                country = "Noruega"
            }
            else -> { // 21
                title = "Pitchstone “Piedra pez”"
                imgName = "pitchstone_piedra_pez.jpg"
                locName = "Isla de Eigg"
                country = "Escocia"
            }
        }

        val imageUrl = "${Constants.IMAGE_BASE_URL}$imgName"
        val state = "Corte: No | Lámina: No"
        val location = "Localidad: $locName, $country"
        val generatedDesc = "Muestra geológica catalogada de tipo **$title**, descubierta y recolectada en la localidad de **$locName**, ubicada en **$country**.\n\n" +
                "**Detalles del Espécimen:**\n" +
                "• **Corte de Exhibición:** Muestra en su estado natural.\n" +
                "• **Estudio en Lámina Delgada:** No disponible para análisis microscópico."

        return RockDetailDto(
            title = title,
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
            images = listOf(imageUrl),
            localities = listOf(locName),
            frequentlyAskedQuestions = emptyList()
        )
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
