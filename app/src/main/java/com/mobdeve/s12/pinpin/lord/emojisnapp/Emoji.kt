package com.mobdeve.s12.pinpin.lord.emojisnapp


data class Emoji(
    val icon: String, // Icon
    val name: String, // Value
    val description: String, //
    val baseCost: Int,  // Default cost
    val basePower: Int, // Default power
    val unlockThreshold: Int // Unlock threshold
) {
    // Modifier that affects total power
    private var modifier: Int = 0
    var currentPower: Int = basePower
    val propertyMap: MutableMap<String, Boolean> = mutableMapOf()

    // Getter for modifier
    fun getModifier(): Int = modifier

    // Function to set modifier, which updates the current power
    fun setModifier(newModifier: Int) {
        modifier = newModifier
        updateCurrentPower()
    }

    // Function to apply the modifier and update the current power
    private fun updateCurrentPower() {
        currentPower = basePower + modifier
    }

}