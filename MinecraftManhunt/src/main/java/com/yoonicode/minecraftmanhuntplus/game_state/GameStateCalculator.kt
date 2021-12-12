package com.yoonicode.minecraftmanhuntplus.game_state

import com.yoonicode.minecraftmanhuntplus.PluginMain
import org.bukkit.Bukkit
import kotlin.math.pow

class GameStateCalculator(private val main: PluginMain) {
    private var gameState: Double
    private var runnerAchievements = 0.0
    private var deaths = 0.0
    private val deathWeight = 1.1
    private val achievementScore = HashMap<Achievement, Int>()

    init { // This field should be from the config.yml
        gameState = main.config.getDouble("gameState")
        for (achievement in Achievement.values()) {
            achievementScore[achievement] = 0
        }
    }

    fun updateAchievement(achievement: Achievement) {
        val level = achievement.achievementLevel
        if (level > gameState.toInt()) {
            achievementScore[achievement] = achievementScore[achievement]!! + 1
            runnerAchievements = level.toDouble()
            if (achievementScore[achievement]!! >= main.runners.size.toDouble() / 2) {
                gameState = calculateGameState()
                main.gameState = gameState
                Bukkit.broadcastMessage("Game state is now: " + main.gameState)
            }
        }
    }

    fun updateDeaths() {
        deaths++
        gameState = calculateGameState()
        main.gameState = gameState
        Bukkit.broadcastMessage("Game state is now: " + main.gameState)
    }

    private fun calculateGameState(): Double {
        return (runnerAchievements + deaths / main.hunters.size.toDouble() * deathWeight.pow(main.runners.size.toDouble())) / 2
    }
}