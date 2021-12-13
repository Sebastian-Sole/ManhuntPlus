package manhunt_plus

import manhunt_plus.SessionHandler.start
import manhunt_plus.SessionHandler.end
import manhunt_plus.SessionHandler.pause
import manhunt_plus.SessionHandler.unPause
import org.bukkit.command.CommandExecutor
import org.bukkit.entity.HumanEntity
import java.util.stream.Collectors
import org.bukkit.entity.Player
import org.bukkit.*
import org.bukkit.inventory.ItemStack
import java.lang.NumberFormatException
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

/**
 * The Commands used in Manhunt
 *
 * @property main main used.
 */
class PluginCommands(private val main: PluginMain) : CommandExecutor {
    private var hitHasRegistered // used for startGameByHit option
            = false
    @JvmField
    var extraDrops = false
    @JvmField
    var chestGenerate = false
    @JvmField
    var compassTask = -1
    private var dangerLevelTask = -1
    @JvmField
    var gameIsRunning = false
    private var worldBorderModified = false
    @JvmField
    var runnerHelp = false
    @JvmField
    var hunterHelp = false
    private var hasteBoost = false
    var isCutClean = false
        private set

    /**
     * Completes the command
     *
     * @param args the command being written
     * @param existingCompletions the completions for the command
     * @return the completions
     */
    fun getCompletions(args: Array<String?>, existingCompletions: List<String>): List<String> {
        return when (args[0]) {
            "/hunter", "/runner", "/spectator" -> {
                Bukkit.getOnlinePlayers().stream().map { obj: HumanEntity -> obj.name }.collect(Collectors.toList())
            }
            "/start", "/end", "/chestgenerate", "/compass", "/clearteams", "/hasteboost", "/allhelp", "/cutclean", "/pause", "/unpause" -> mutableListOf()
            "/setheadstart" -> {
                mutableListOf("0", "30", "60")

            }
            else -> existingCompletions
        }
    }

    /**
     * When a command is run inside minecraft
     *
     * @param commandSender the commandSender sending the command (is a player)
     * @param command I have no idea
     * @param label the first part that comes after the "/". E.g., "/hunter" will have label hunter
     * @param args a list of after the first part of the command.
     * @return a boolean for whether the command worked or not.
     */
    override fun onCommand(commandSender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        when (label) {
            "hunter" -> {
                if (args.size != 1) return false
                val target = Bukkit.getPlayer(args[0])
                if (target == null) {
                    commandSender.sendMessage("Target is not online")
                    return false
                }
                if (isOnTargetTeam(commandSender, target, main.hunters)) return true
                main.runners.remove(target)
                main.spectators.remove(target)
                main.hunters.add(target)
                main.hunterDeaths[target] = 0
                Bukkit.broadcastMessage(target.name + " is now a " + ChatColor.RED + ChatColor.BOLD + "HUNTER!")
                return true
            }
            "runner" -> {
                if (args.size != 1) return false
                val target = Bukkit.getPlayer(args[0])
                if (target == null) {
                    commandSender.sendMessage("Target is not online")
                    return false
                }
                if (isOnTargetTeam(commandSender, target, main.runners)) return true
                main.hunters.remove(target)
                main.spectators.remove(target)
                main.runners.add(target)
                main.runnerDeaths[target] = 0
                Bukkit.broadcastMessage(target.name + " is now a " + ChatColor.GREEN + ChatColor.BOLD + "RUNNER!")
                return true
            }
            "spectator" -> {
                if (args.size != 1) return false
                val target = Bukkit.getPlayer(args[0])
                if (target == null) {
                    commandSender.sendMessage("Target is not online")
                    return false
                }
                if (isOnTargetTeam(commandSender, target, main.spectators)) return true
                main.runners.remove(target)
                main.hunters.remove(target)
                main.spectators.add(target)
                target.sendMessage("You have been marked as a spectator.")
                commandSender.sendMessage("Marked player as spectator")
                return true
            }
            "start" -> {
//            if (!main.getConfig().getBoolean("gameIsOver")){
//                commandSender.sendMessage("This game is paused, do not use /start. Use /unpause to continue.");
//                return true;
//            }
                if (gameIsRunning) {
                    commandSender.sendMessage("Game is already in progress. Use /end before starting another game.")
                    return true
                }
                if (main.runners.size < 1 && !main.debugMode) {
                    commandSender.sendMessage("Not enough speedrunners to start")
                    return true
                }
                commandSender.sendMessage("Starting game...")
                main.targets.clear()
                val headStartDuration = main.config.getInt("headStartDuration")
                if (main.config.getBoolean("clearItemDropsOnStart", false)) {
                    commandSender.server.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:kill @e[type=item]")
                }
                updateWorld()
                updateSpectators()
                runnersState()
                huntersState(headStartDuration)
                //            teleportPlayers();
                compassTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, { main.taskManager.updateCompass() }, 0L, 20L)
                if (hasteBoost) {
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(main, { main.taskManager.giveHaste() }, 460L, 1200L)
                }

                //todo: How often does this actually need to repeat?
//            Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
//                @Override
//                public void run() {
//                    main.getTaskManager().showGlow();
//                }
//            }, 0,600); // 2700? More?
                gameIsRunning = true
                sendStartMessage()
                start(main)
                return true
            }
            "end" -> {
                if (!gameIsRunning) {
                    commandSender.sendMessage("There is no game in progress. Use /start to start a new game.")
                    return true
                }
                if (main.config.getBoolean("startGameByHit", false)) {
                    hitHasRegistered = false
                }
                val scheduler = Bukkit.getScheduler()
                if (compassTask != -1) {
                    scheduler.cancelTask(compassTask)
                    compassTask = -1
                }
                if (dangerLevelTask != -1) {
                    scheduler.cancelTask(dangerLevelTask)
                    dangerLevelTask = -1
                }
                worldBorderModified = false
                Bukkit.broadcastMessage("Manhunt stopped!")
                gameIsRunning = false
                end(main)
                return true
            }
            "compass" -> {
                if (args.isNotEmpty()) {
                    commandSender.sendMessage("Illegal format, please use /compass")
                }
                val sender = commandSender as Player
                sender.inventory.addItem(ItemStack(Material.COMPASS, 1))
                commandSender.sendMessage("Here you go!")
                return true
            }
            "clearteams" -> {
                commandSender.sendMessage(clearTeams())
                return true
            }
            "setheadstart" -> {
                main.mainLogger.info("setheadstart called.")
                if (args.isEmpty()) {
                    commandSender.sendMessage("Provide a headstart duration as a nonnegative integer")
                    return false
                }
                val duration: Int = try {
                    args[0].toInt()
                } catch (e: NumberFormatException) {
                    commandSender.sendMessage("Headstart duration must be a nonnegative integer")
                    return false
                }
                if (duration < 0) {
                    commandSender.sendMessage("Headstart duration must be greater than or equal to 0")
                    return false
                }
                main.config["headStartDuration"] = duration
                main.saveConfig()
                commandSender.sendMessage("Headstart set to $duration")
                return true
            }
            "runnerhelp" -> {
                if (illegalCommandCall(commandSender, args, "runnerhelp")) return true
                runnerHelp = !runnerHelp
                commandSender.sendMessage("Runner help is set to: $runnerHelp")
            }
            "hunterhelp" -> {
                if (illegalCommandCall(commandSender, args, "hunterhelp")) return true
                hunterHelp = !hunterHelp
                commandSender.sendMessage("Hunter help is set to: $hunterHelp")
            }
            "extradrops" -> {
                if (illegalCommandCall(commandSender, args, "extradrops")) return true
                extraDrops = !extraDrops
                commandSender.sendMessage("Extra drops is set to: $extraDrops")
            }
            "chestgenerate" -> {
                if (illegalCommandCall(commandSender, args, "chestgenerate")) return true
                chestGenerate = !chestGenerate
                commandSender.sendMessage("Random Chest spawns is set to: $chestGenerate")
                return true
            }
            "hasteboost" -> {
                if (illegalCommandCall(commandSender, args, "hasteboost")) return true
                hasteBoost = !hasteBoost
                commandSender.sendMessage("Haste boost is set to: $hasteBoost")
                return true
            }
            "allhelp" -> {
                if (illegalCommandCall(commandSender, args, "allhelp")) return true
                runnerHelp = true
                hunterHelp = true
                extraDrops = true
                chestGenerate = true
                hasteBoost = true
                isCutClean = true
                commandSender.sendMessage("All helper methods are enabled")
                return true
            }
            "cutclean" -> {
                if (illegalCommandCall(commandSender, args, "cutclean")) return true
                isCutClean = !isCutClean
                commandSender.sendMessage("Cut Clean is set to: $isCutClean")
                return true
            }
            "pause" -> {
                if (args.isNotEmpty()) {
                    return true
                }
                pause(main)
            }
            "unpause" -> {
                if (args.isNotEmpty()) {
                    return true
                }
                unPause(main)
            }
        }
        return false
    }

    //    private void teleportPlayers() {
    //        for (Player player : Bukkit.getOnlinePlayers()){
    //            player.teleport(player.getWorld().getSpawnLocation());
    //        }
    //    }

    /**
     * Checks if the command call is illegal
     *
     * @param commandSender the sender of the command
     * @param args a list of after the first part of the command.
     * @param command The command being sent
     * @return a boolean for whether the call is illegal or not
     */
    private fun illegalCommandCall(commandSender: CommandSender, args: Array<String>, command: String): Boolean {
        gameInSession(commandSender)
        if (args.isNotEmpty()) {
            commandSender.sendMessage("Illegal format. Use /$command.")
            return true
        }
        return false
    }

    /**
     * Check if the game is running
     *
     * @param commandSender the sender of the command.
     */
    private fun gameInSession(commandSender: CommandSender) {
        if (gameIsRunning) {
            commandSender.sendMessage("Game is already in progress. Restart a game to change this option")
        }
    }

    /**
     * Check if the target of the command is on the desired team already.
     *
     * @param commandSender the sender of the command.
     * @param target the target of the command.
     * @param team the target team.
     * @return true if the target is on that team.
     */
    private fun isOnTargetTeam(commandSender: CommandSender, target: Player, team: List<Player>): Boolean {
        if (team.stream().anyMatch { player: Player -> player.name == target.name }) {
            commandSender.sendMessage("Target is already on this team")
            return true
        }
        return false
    }

    /**
     * Send the message on start of a manhunt game
     */
    private fun sendStartMessage() {
        Bukkit.broadcastMessage(
                """${ChatColor.DARK_RED}${ChatColor.UNDERLINE}Berner er gay!${ChatColor.RESET}
                    ${ChatColor.AQUA}Tarun ${ChatColor.GOLD}Tarun${ChatColor.AQUA} Tarun
                    ${ChatColor.GREEN}TarunTårnet! ${ChatColor.GOLD}TARUNSAN! TARUNSAN! TARUNSAN!${ChatColor.GREEN} BI is love, BI is life -Tarun, probably
                    ${ChatColor.UNDERLINE}${ChatColor.BOLD}${ChatColor.LIGHT_PURPLE}MANHUNT STARTED!${ChatColor.RESET}${ChatColor.DARK_GRAY} NTarunNU"""
        )
    }

    /**
     * The hunters state when the game is started.
     *
     * @param headStartDuration the duration that hunters must wait.
     */
    private fun huntersState(headStartDuration: Int) {
        for (player in main.hunters) {
            player.gameMode = GameMode.SURVIVAL
            player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20 * headStartDuration, 5))
            player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20 * headStartDuration, 3))
            player.addPotionEffect(PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * headStartDuration, 10))
            player.health = 20.0
            player.foodLevel = 20
            if (main.config.getBoolean("clearHunterInvOnStart", false)) {
                player.inventory.clear()
                player.exp = 0f
                player.level = 0
            }
            player.inventory.addItem(ItemStack(Material.COMPASS, 1))
        }
    }

    /**
     * The runners state when the game is started.
     */
    @Suppress("DEPRECATION")
    private fun runnersState() {
        for (player in main.runners) {
            player.gameMode = GameMode.SURVIVAL
            player.healthScale = 20.0
            player.maxHealth = 20.0
            player.health = 20.0
            player.foodLevel = 20
            player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 450, 1))
            if (main.config.getBoolean("clearRunnerInvOnStart", false)) {
                player.inventory.clear()
                player.exp = 0f
                player.level = 0
            }
        }
    }

    /**
     * Set gamemode for spectators
     */
    private fun updateSpectators() {
        for (player in main.spectators) {
            player.gameMode = GameMode.SPECTATOR
        }
    }

    /**
     * Update the world border and time
     */
    private fun updateWorld() {
        if (worldBorderModified) {
            val wb = main.world?.worldBorder
            wb?.setCenter(0.5, 0.5)
            wb?.size = 60000000.0
        }
        if (main.config.getBoolean("setTimeToZero", true)) {
            main.world?.time = 0
            main.world?.clearWeatherDuration = 199999999
        }
    }

    /**
     * Cleat the teams
     *
     * @return a string message to the players
     */
    private fun clearTeams(): String {
        val playersCleared = main.hunters.size + main.spectators.size + main.runners.size
        main.hunters.clear()
        main.spectators.clear()
        main.runners.clear()
        return "$playersCleared players cleared from teams"
    }

    companion object {
        @JvmField
        val registeredCommands = arrayOf(
                "hunter",
                "runner",
                "spectator",
                "clearteams",
                "start",
                "end",
                "compass",
                "setheadstart",
                "runnerhelp",
                "hunterhelp",
                "extradrops",
                "chestgenerate",
                "hasteboost",
                "allhelp",
                "cutclean",
                "pause",
                "unpause"
        )
    }
}