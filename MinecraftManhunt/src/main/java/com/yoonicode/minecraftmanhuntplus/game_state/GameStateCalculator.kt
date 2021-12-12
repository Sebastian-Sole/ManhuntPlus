package com.yoonicode.minecraftmanhuntplus.game_state

import com.yoonicode.minecraftmanhuntplus.PluginMain
import org.bukkit.Bukkit
import kotlin.math.pow

/**
 * TODO
 * Calculates the game state.
 * @property main the main plugin
 */
class GameStateCalculator(private val main: PluginMain) {
    private var gameState: Double
    private var runnerAchievements = 0.0
    private var deaths = 0.0
    private val deathWeight = 1.1
    private val advancementValueScore = HashMap<AdvancementValue, Int>()

    init {
        gameState = main.config.getDouble("gameState")
        for (achievement in AdvancementValue.values()) {
            advancementValueScore[achievement] = 0
        }
    }

    /**
     * Updates the game state based on an achievement.
     *
     * @param advancementValue the achievement value that the game state should be updated by.
     */
    fun updateAchievement(advancementValue: AdvancementValue) {
        val level = advancementValue.achievementLevel
        if (level > gameState.toInt()) {
            advancementValueScore[advancementValue] = advancementValueScore[advancementValue]!! + 1
            runnerAchievements = level.toDouble()
            if (advancementValueScore[advancementValue]!! >= main.runners.size.toDouble() / 2) {
                gameState = calculateGameState()
                main.gameState = gameState
                Bukkit.broadcastMessage("Game state is now: " + main.gameState)
            }
        }
    }

    /**
     * Updates the game state based on a death
     */
    fun updateDeaths() {
        deaths++
        gameState = calculateGameState()
        main.gameState = gameState
        Bukkit.broadcastMessage("Game state is now: " + main.gameState)
    }

    /**
     * Calculate the game score based on the formula.
     *
     * @return game score
     */
    private fun calculateGameState(): Double {
        return (runnerAchievements + deaths / main.hunters.size.toDouble() * deathWeight.pow(main.runners.size.toDouble())) / 2
    }
}