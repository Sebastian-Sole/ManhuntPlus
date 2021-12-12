package com.yoonicode.minecraftmanhuntplus

import org.bukkit.Bukkit

object PauseHandler {
    //todo: add config data for deaths, runner advancements,
    @JvmStatic
    fun pause(main: PluginMain) {
        main.config["gameState"] = main.gameState
        main.config["paused"] = true
        main.saveConfig()
        Bukkit.broadcastMessage("Game pause, game state is: " + main.gameState)
    }

    @JvmStatic
    fun unPause(main: PluginMain) {
        main.gameState = main.config.getDouble("gameState")
        main.config["pause"] = false
        main.saveConfig()
        Bukkit.broadcastMessage("Game unpaused, game state is: " + main.gameState)
    }

    @JvmStatic
    fun start(main: PluginMain) {
        // If starting the plugin after a server stop, but also after a pause
        if (main.config.getBoolean("paused")) {
            unPause(main)
        } else {
            main.gameState = 0.0
            main.config["gameState"] = 0
            main.config["paused"] = false
            main.saveConfig()
            Bukkit.broadcastMessage("Game state: " + main.gameState)
        }
    }

    @JvmStatic
    fun end(main: PluginMain) {
        main.gameState = 0.0
        main.config["gameState"] = main.gameState
        main.config["paused"] = false
        main.saveConfig()
        Bukkit.broadcastMessage("Game ended, game state is: " + main.gameState)
    }

    @JvmStatic
    fun onEnable(main: PluginMain) {
        if (main.isGameIsOver) {
            main.config["gameOver"] = false
            main.saveConfig()
        }
        main.logger.info("Game state is " + main.gameState + ", should be: " + main.config.getInt("gamesState"))
        main.logger.info("Paused is " + main.isPaused + ", should be: " + main.config.getBoolean("paused"))
        main.logger.info("Game over state is " + main.isGameIsOver + ", should be: " + main.config.getBoolean("gameOver"))
    }

    @JvmStatic
    fun onDisable(main: PluginMain) {
        if (main.isGameIsOver) {
            end(main)
        } else {
            pause(main)
        }
    }

    // This could cause problems
    @JvmStatic
    fun updateGame(main: PluginMain) {
        main.config["gameState"] = main.gameState
        main.config["paused"] = main.isPaused
        main.config["gameOver"] = main.isGameIsOver
        main.saveConfig()
    }
}