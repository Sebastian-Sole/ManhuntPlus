package manhunt_plus

import org.bukkit.Bukkit

/**
 * Handles the state of the game session, such as starting, stopping, pausing, etc.
 */

object SessionHandler {
    //todo: add config data for deaths, runner advancements,

    /**
     * Pause the game.
     *
     * @param main main used.
     */
    @JvmStatic
    fun pause(main: PluginMain) {
        main.config["gameState"] = main.gameState
        main.config["paused"] = true
        main.saveConfig()
        Bukkit.broadcastMessage("Game pause, game state is: " + main.gameState)
    }

    /**
     * Unpause the game.
     *
     * @param main main used.
     */

    @JvmStatic
    fun unPause(main: PluginMain) {
        main.gameState = main.config.getDouble("gameState")
        main.config["pause"] = false
        main.saveConfig()
        Bukkit.broadcastMessage("Game unpaused, game state is: " + main.gameState)
    }

    /**
     * Start the game.
     *
     * @param main main used.
     */

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


    /**
     * Ends the game.
     *
     * @param main main used.
     */
    @JvmStatic
    fun end(main: PluginMain) {
        main.gameState = 0.0
        main.config["gameState"] = main.gameState
        main.config["paused"] = false
        main.saveConfig()
        Bukkit.broadcastMessage("Game ended, game state is: " + main.gameState)
    }

    /**
     * When the plugin is enabled (when the server is started).
     *
     * @param main main used.
     */
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

    /**
     * When the plugin is disabled (when the server is closed).
     *
     * @param main main used.
     */
    @JvmStatic
    fun onDisable(main: PluginMain) {
        if (main.isGameIsOver) {
            end(main)
        } else {
            pause(main)
        }
    }

    /**
     * Updates the game state
     *
     * @param main main used.
     */
    // This could cause problems
    @JvmStatic
    fun updateGame(main: PluginMain) {
        main.config["gameState"] = main.gameState
        main.config["paused"] = main.isPaused
        main.config["gameOver"] = main.isGameIsOver
        main.saveConfig()
    }
}