package com.yoonicode.minecraftmanhuntplus

import com.yoonicode.minecraftmanhuntplus.SessionHandler.onEnable
import com.yoonicode.minecraftmanhuntplus.SessionHandler.onDisable
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.entity.Player
import java.util.HashMap
import org.bukkit.World
import com.yoonicode.minecraftmanhuntplus.respawn_inventory.InventoryGenerator
import com.yoonicode.minecraftmanhuntplus.game_state.GameStateCalculator
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.logging.Logger

class PluginMain : JavaPlugin() {
    @JvmField
    var hunters = mutableListOf<Player>()
    @JvmField
    var runners = mutableListOf<Player>()
    var spectators = mutableListOf<Player>()
    @JvmField
    var targets = HashMap<String, String>()
    @JvmField
    var portals = HashMap<String, Location>()
    var world: World? = null;
    var commands: PluginCommands = PluginCommands(this)
    var debugMode = false

    var mainLogger: Logger = Logger.getLogger("com.yoonicode.minecraftmanhuntplus.PluginMain")

    @JvmField
    var compassEnabledInNether = false
    var hunterDeaths = HashMap<Player, Int>()
    var runnerDeaths = HashMap<Player, Int>()
    val taskManager = TaskManager(this)
    var gameState = 0.0
    var itemGenerator: InventoryGenerator = InventoryGenerator(this)

    var isPaused = false
    var isGameIsOver = false
        set(gameIsOver) {
            field = gameIsOver
            config["gameOver"] = true
            saveConfig()
        }
    var gameStateCalculator: GameStateCalculator = GameStateCalculator(this)

    fun playerIsOnTeam(player: Player): Boolean {
        return (hunters.stream().anyMatch { member: Player -> member.name == player.name }
                || runners.stream().anyMatch { member: Player -> member.name == player.name }
                || spectators.stream().anyMatch { member: Player -> member.name == player.name })
    }

    /**
     * When the plugin is enabled (when the server is started)
     */
    override fun onEnable() {
        mainLogger.info("Minecraft Manhunt plugin enabled!")
        val worlds = Bukkit.getWorlds()
        if (worlds.size < 1) {
            mainLogger.warning("Could not detect main world! Plugin will not work.")
        }
        world = worlds[0]
        saveDefaultConfig()
        debugMode = config.getBoolean("debugMode", false)
        compassEnabledInNether = config.getBoolean("compassEnabledInNether", true)
        server.pluginManager.registerEvents(PluginListener(this), this)
        commands = PluginCommands(this)
        for (command in PluginCommands.registeredCommands) {
            getCommand(command)?.setExecutor(commands)
        }
        onEnable(this)
    }

    /**
     * When the plugin is disabled (when the server is closed)
     */
    override fun onDisable() {
        mainLogger.info("Minecraft Manhunt plugin disabled!")
        onDisable(this)
    }

}