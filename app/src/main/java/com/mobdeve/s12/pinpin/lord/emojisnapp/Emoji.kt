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
    val activeEffects: MutableMap<String, Boolean> = mutableMapOf()

    // Getter for modifier
    fun getModifier(): Int = modifier

    // Function to set modifier, which updates the current power
    fun setModifier(newModifier: Int) {
        modifier = newModifier
        updateCurrentPower()
    }

    // Function to apply the modifier and update the current power
    fun updateCurrentPower() {
        var currentTotal = basePower + modifier

        // Apply effects
        if (activeEffects["Dumpster"] == true) {
            currentTotal -= 3
        }
        if (activeEffects["Castle"] == true) {
            currentTotal += 2
        }

        if (activeEffects["Galaxy"] == false) {
            // TODO: Emoji effects

        }

        if (activeEffects["Arena"] == true) {
            currentTotal *= 2
        }



        currentPower = currentTotal


    }

}