package com.example.herometrics.api.armory

data class CharacterStatsResponse(
    val health: Int,
    val power: Int,
    val power_type: PowerType, // Aquí lo tratamos como un objeto para obtener 'name'
    val versatility: Double,
    val strength: Attribute,
    val agility: Attribute,
    val intellect: Attribute,
    val melee_crit: RatingValue,
    val mastery: RatingValue
)

data class PowerType(
    val name: String
)

data class Attribute(
    val base: Int,
    val effective: Int
)

data class RatingValue(
    val rating_bonus: Double,
    val value: Double
)
