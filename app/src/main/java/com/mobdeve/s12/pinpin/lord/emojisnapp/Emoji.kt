package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.util.Log


data class Emoji(
    val icon: String, // Icon
    val name: String, // Value
    val description: String, //
    val baseCost: Int,  // Default cost
    val basePower: Int, // Default power
    val unlockThreshold: Int // Unlock threshold
) {



    // Modifier that affects total power
    private var modifier: MutableList<Int> = mutableListOf()
    var currentPower: Int = basePower
    val activeEffects: MutableMap<String, Boolean> = mutableMapOf()
    var isDestroyed: Boolean = false
    var additive: Int = 0


    // Getter for modifier
    fun getModifier(): List<Int> = modifier

    // Function to set modifier, which updates the current power
    fun addModifier(newModifier: Int) {
        modifier.add(newModifier)
        updateCurrentPower()
    }

    // Function to apply the modifier and update the current power
    fun updateCurrentPower() {
        var currentTotal = basePower + modifier.sum()

        // Dumpster adds -3 to all Emojis in its location
        if (activeEffects["Dumpster"] == true) {
            currentTotal -= 3
        }
        // Castle adds 2 to all Emojis in its location
        if (activeEffects["Castle"] == true) {
            currentTotal += 2
        }

        // Galaxy disables all passive effects
        if (!(activeEffects["Galaxy"] == true)) {
            // TODO: Emoji effects
            // Rainbow adds +1 to all emojis in its location
            if (activeEffects["Rainbow"] == true) {
                currentTotal += 1
            }
            // Angel Face prevents any negative modifiers.
            if (activeEffects["Angel Face"] == false) {
                if (activeEffects["Hundred Points"] == true) {
                    currentTotal -= 1
                }
            }
            if (activeEffects["Glowing Star"] == true) {
                currentTotal += 2
            }
        }

        // All negative modifiers are counteracted (if Angel Face is somehow removed, this effect should not remain)
        if (activeEffects["Angel Face"] == true){
            modifier.forEach {
                if (it < 0) {
                    currentTotal += Math.abs(it)  // Add the opposite value of negative modifiers
                }
            }
        }

        // Hundred Points gets +1 for each odd-Power emoji
        if (name == "Hundred Points"){
            currentTotal += additive
        }

        if (name == "Strong Arm"){
            Log.d("Emoji", "Strong Arm Check")
            Log.d("Emoji", "Strong Arm = $currentTotal")
            currentTotal -= basePower
            Log.d("Emoji", "Strong Arm = $currentTotal")
            currentTotal *= 2
            Log.d("Emoji", "Strong Arm = $currentTotal")
            currentTotal += basePower
            Log.d("Emoji", "Strong Arm = $currentTotal")
        }

        if (activeEffects["Arena"] == true) {
            currentTotal *= 2
        }



        currentPower = currentTotal


    }

}