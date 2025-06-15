package com.example.herometrics.api.armory

data class CharacterStatsResponse(
    val health: Int,
    val strength: Attribute,
    val agility: Attribute,
    val intellect: Attribute,
    val versatility: Double,
    val melee_crit: RatingValue,
    val mastery: RatingValue,
    val melee_haste: RatingValue
)

data class CharacterViewData(
    val name: String,
    val spec: String,
    val itemLevel: Int,
    val imageUrl: String,
    val crit: Double,
    val haste: Double,
    val mastery: Double,
    val versatility: Double
)

data class Attribute(
    val base: Int,
    val effective: Int
)

data class Reino(
    val slug: String
)

data class RatingValue(
    val rating_bonus: Double,
    val value: Double,
    val rating_normalized: Double
)

data class CharacterProfileResponse(
    val name: String,
    val realm: Reino,
    val active_spec: Specialization?
)

data class Specialization(
    val name: String
)

data class CharacterEquipmentResponse(
    val equipped_item_level: Int
)

data class CharacterMediaResponse(
    val assets: List<Asset>
)

data class Asset(
    val key: String,
    val value: String // URL de la imagen renderizada completa
)