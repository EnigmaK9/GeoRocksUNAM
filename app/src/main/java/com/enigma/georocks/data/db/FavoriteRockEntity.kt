// ===============================================================
// File path: app/src/main/java/com/enigma/georocks/data/db/FavoriteRockEntity.kt
// This entity is created to store minimal information about a favorited rock.
//
// It is recommended to add columns as needed if more info is to be shown offline.
//
// Comments are written in passive voice for debugging convenience.
// ===============================================================

package com.enigma.georocks.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_rocks")
// This entity class is declared for Room to store favorites locally
data class FavoriteRockEntity(
    @PrimaryKey
    // This field is used for storing the unique rock ID
    val rockId: String,
    // This field is used for storing the rock's title
    val title: String?,
    // This field is used for storing the rock's thumbnail
    val thumbnail: String?
)
