package com.mobdeve.s12.pinpin.lord.emojisnapp


data class Emoji(
    val icon: String, // Icon
    val name: String, // Value
    val description: String, //
    val baseCost: Int,  // Default cost
    val basePower: Int, // Default power
    val unlockThreshold: Int // Unlock threshold
) {
    // Modifier that affect total power
    var modifier: Int = 0
        private set
    var currentPower: Int = basePower
        private set
    val propertyMap: MutableMap<String, Boolean> = mutableMapOf()

    // Function to create a copy of the emoji with modified properties
    fun copy(): Emoji {
        return this.copy()
    }
}