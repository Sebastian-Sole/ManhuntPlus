package manhunt_plus

import manhunt_plus.chest_generation.generateChestItems
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.block.CreatureSpawner
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.CompassMeta
import org.bukkit.potion.PotionEffectType
import java.util.*
import java.util.stream.Collectors
import kotlin.math.roundToInt

/**
 * Handle running tasks
 *
 * @property main the main plugin
 */
class TaskManager(private val main: PluginMain) {

    /**
     * Update's the compass
     */
    fun updateCompass() {
        for ((key, value) in main.targets) {
            val hunter = Bukkit.getPlayer(key)
            val target = Bukkit.getPlayer(value)
            if (hunter == null || target == null) {
                continue
            }
            if (!main.playerIsOnTeam(hunter)) {
                continue
            }
            val inv = hunter.inventory
            // If the hunter and runner are not in the same world, point compass to the location of the portal (in the hunter's world)
            if (hunter.world.environment != target.world.environment) {
                val portalLocation: Location?
                // If hunter is in nether and runner is not in nether, show the portal in the nether
                if (hunter.world.environment == World.Environment.NETHER && target.world.environment != World.Environment.NETHER){
                    portalLocation = main.netherPortals[target.name]
                }
                // If hunter is in overworld, and runner is not in overworld
                else if (hunter.world.environment == World.Environment.NORMAL && target.world.environment != World.Environment.NORMAL){
                    portalLocation = main.overworldPortals[target.name]
                }
                else{
                    portalLocation = hunter.world.spawnLocation
                }

                if (portalLocation != null) {
                    hunter.compassTarget = portalLocation
                }
                // Add enchant to compass
                for (j in 0 until inv.size) {
                    val stack = inv.getItem(j) ?: continue
                    if (stack.type != Material.COMPASS) continue
                    stack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1) // Make all compasses glow
                    val meta = stack.itemMeta
                    meta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                    stack.itemMeta = meta
                }
            }
            // If hunter and runner are in the same world, point compass to the location of the runner
            else {
                hunter.compassTarget = target.location
                if (main.compassEnabledInNether) {
                    for (j in 0 until inv.size) {
                        val stack = inv.getItem(j) ?: continue
                        if (stack.type != Material.COMPASS) continue
                        val meta = stack.itemMeta as CompassMeta?
                        meta!!.lodestone = target.location
                        meta.isLodestoneTracked = false
                        stack.itemMeta = meta
                    }
                }
            }
        }
    }

    /**
     * Gives haste
     */
    fun giveHaste() {
        for (player in main.hunters) {
            player.player!!.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(Int.MAX_VALUE, 3))
        }
        for (player in main.runners) {
            player.player!!.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(Int.MAX_VALUE, 3))
        }
    } //

    /**
     * Displays the coordinates of the closest teammate in the action bar
     *
     * @param player the player receiving the coordinates
     * @param teammates the teammates
     */
    fun updateActionBar(player: Player, teammates: MutableList<Player> ) {
        val playerX = player.location.x
        val playerZ = player.location.z

        val teammateLocation = closestTeammateCoords(playerX, playerZ, teammates)
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(
            """ ${ChatColor.BOLD} ${ChatColor.RED} x: ${teammateLocation?.x?.roundToInt()}, y: ${teammateLocation?.y?.roundToInt()}, z: ${teammateLocation?.z?.roundToInt()}"""));
    }

    /**
     * Returns the location to the closest teammate
     *
     * @param playerX the player's X coordinate
     * @param playerZ the player's Z coordinate
     * @param teammates the teammates of that player
     * @return the location of the closest teammate, or the world's spawn-point if the player has no teammates
     */
    private fun closestTeammateCoords(playerX: Double, playerZ: Double, teammates: MutableList<Player>): Location? {
        var closestLocation: Location? = null;
        var closestDistance: Double = Double.MAX_VALUE
        // If the player has no teammates
        if (teammates.size == 1){
            return main.world?.spawnLocation
        }
        // Find the closest teammate
        for (player in teammates){

            val xAxisDifference = player.location.x - playerX
            val zAxisDifference = player.location.z - playerZ

            val totalDifference = xAxisDifference + zAxisDifference
            if (totalDifference < closestDistance && totalDifference != 0.0){
                closestLocation = player.location
                closestDistance = totalDifference
            }
        }

        return closestLocation
    }

    fun supplyDrop() {
        var hunterCoords = teamCoords(main.hunters)
        val runnerCoords = teamCoords(main.runners)
        val targetWorld: World? = runnerCoords.world // Supply drops will always spawn in the runner's world
        // Don't drop supply drop in the end
        if (targetWorld?.environment == World.Environment.THE_END){
            return
        }
        // If hunters in overworld, and runners in nether
        if (hunterCoords.world?.environment == World.Environment.NORMAL && runnerCoords.world?.environment == World.Environment.NETHER){
            val runnerInNether: Player = main.runners.stream().filter{ player -> player.world.environment == World.Environment.NETHER }.collect(Collectors.toList())[0]
            hunterCoords = main.netherPortals[runnerInNether.name]!!
        }
        else if (hunterCoords.world?.environment == World.Environment.NETHER && runnerCoords.world?.environment == World.Environment.NORMAL){
            val runnerInOverWorld: Player = main.runners.stream().filter{ player -> player.world.environment == World.Environment.NORMAL }.collect(Collectors.toList())[0]
            hunterCoords = main.overworldPortals[runnerInOverWorld.name]!!
        }

        val middleX = ((hunterCoords.x + runnerCoords.x)/2).roundToInt().toDouble()
        val middleY = ((hunterCoords.y + runnerCoords.y)/2).roundToInt().toDouble()
        val middleZ = ((hunterCoords.z + runnerCoords.z)/2).roundToInt().toDouble()

        val supplyDropLocation: Location = Location(targetWorld,middleX,middleY,middleZ)
        targetWorld?.let { createSupplyDrop(supplyDropLocation, it) }

        // get the average position of each team
        // how should this be implemented if the teams are in two different worlds?
    }

    fun createSupplyDrop(location: Location, world: World) {
        createChestDrop(world, location)
        generateBox(world,location)
        Bukkit.broadcastMessage("A supply drop has landed at: ${location.x}, ${location.y}, ${location.z}")
    }

    private fun generateBox(world: World, chestBlockLocation: Location) {
        // x = +3
        for (i in 0 .. 3){
            for (j in 0..3) {
                // x = +3
                world.getBlockAt((chestBlockLocation.x + 3).toInt(), (chestBlockLocation.y+i).toInt(), (chestBlockLocation.z+j).toInt()).type = Material.OBSIDIAN
                world.getBlockAt((chestBlockLocation.x + 3).toInt(), (chestBlockLocation.y+i).toInt(), (chestBlockLocation.z-j).toInt()).type = Material.OBSIDIAN
                world.getBlockAt((chestBlockLocation.x + 3).toInt(), (chestBlockLocation.y-i).toInt(), (chestBlockLocation.z+j).toInt()).type = Material.OBSIDIAN
                world.getBlockAt((chestBlockLocation.x + 3).toInt(), (chestBlockLocation.y-i).toInt(), (chestBlockLocation.z-j).toInt()).type = Material.OBSIDIAN
                // x = -3
                world.getBlockAt((chestBlockLocation.x - 3).toInt(), (chestBlockLocation.y+i).toInt(), (chestBlockLocation.z+j).toInt()).type = Material.OBSIDIAN
                world.getBlockAt((chestBlockLocation.x - 3).toInt(), (chestBlockLocation.y+i).toInt(), (chestBlockLocation.z-j).toInt()).type = Material.OBSIDIAN
                world.getBlockAt((chestBlockLocation.x - 3).toInt(), (chestBlockLocation.y-i).toInt(), (chestBlockLocation.z+j).toInt()).type = Material.OBSIDIAN
                world.getBlockAt((chestBlockLocation.x - 3).toInt(), (chestBlockLocation.y-i).toInt(), (chestBlockLocation.z-j).toInt()).type = Material.OBSIDIAN

                // z = +3
                world.getBlockAt((chestBlockLocation.x + j).toInt(), (chestBlockLocation.y+i).toInt(), (chestBlockLocation.z+3).toInt()).type = Material.OBSIDIAN
                world.getBlockAt((chestBlockLocation.x - j).toInt(), (chestBlockLocation.y+i).toInt(), (chestBlockLocation.z+3).toInt()).type = Material.OBSIDIAN
                world.getBlockAt((chestBlockLocation.x + j).toInt(), (chestBlockLocation.y-i).toInt(), (chestBlockLocation.z+3).toInt()).type = Material.OBSIDIAN
                world.getBlockAt((chestBlockLocation.x - j).toInt(), (chestBlockLocation.y-i).toInt(), (chestBlockLocation.z+3).toInt()).type = Material.OBSIDIAN

                // z = -3
                world.getBlockAt((chestBlockLocation.x + j).toInt(), (chestBlockLocation.y+i).toInt(), (chestBlockLocation.z-3).toInt()).type = Material.OBSIDIAN
                world.getBlockAt((chestBlockLocation.x - j).toInt(), (chestBlockLocation.y+i).toInt(), (chestBlockLocation.z-3).toInt()).type = Material.OBSIDIAN
                world.getBlockAt((chestBlockLocation.x + j).toInt(), (chestBlockLocation.y-i).toInt(), (chestBlockLocation.z-3).toInt()).type = Material.OBSIDIAN
                world.getBlockAt((chestBlockLocation.x - j).toInt(), (chestBlockLocation.y-i).toInt(), (chestBlockLocation.z-3).toInt()).type = Material.OBSIDIAN

                // y = +3
                world.getBlockAt((chestBlockLocation.x + j).toInt(), (chestBlockLocation.y+3).toInt(), (chestBlockLocation.z+i).toInt()).type = Material.OBSIDIAN
                world.getBlockAt((chestBlockLocation.x - j).toInt(), (chestBlockLocation.y+3).toInt(), (chestBlockLocation.z+i).toInt()).type = Material.OBSIDIAN
                world.getBlockAt((chestBlockLocation.x + j).toInt(), (chestBlockLocation.y+3).toInt(), (chestBlockLocation.z-i).toInt()).type = Material.OBSIDIAN
                world.getBlockAt((chestBlockLocation.x - j).toInt(), (chestBlockLocation.y+3).toInt(), (chestBlockLocation.z-i).toInt()).type = Material.OBSIDIAN

                // y = -3
                world.getBlockAt((chestBlockLocation.x + j).toInt(), (chestBlockLocation.y-3).toInt(), (chestBlockLocation.z+i).toInt()).type = Material.OBSIDIAN
                world.getBlockAt((chestBlockLocation.x - j).toInt(), (chestBlockLocation.y-3).toInt(), (chestBlockLocation.z+i).toInt()).type = Material.OBSIDIAN
                world.getBlockAt((chestBlockLocation.x + j).toInt(), (chestBlockLocation.y-3).toInt(), (chestBlockLocation.z-i).toInt()).type = Material.OBSIDIAN
                world.getBlockAt((chestBlockLocation.x - j).toInt(), (chestBlockLocation.y-3).toInt(), (chestBlockLocation.z-i).toInt()).type = Material.OBSIDIAN

            }
        }

    }

    private fun createChestDrop(world: World, location: Location) {
        val block = world.getBlockAt(location)
        block.type = Material.CHEST
        val chest = block.state as Chest
        val inv = chest.inventory
        val itemsList = generateChestItems()
        for (stack in itemsList) {
            inv.addItem(stack)
        }
    }

    private fun teamCoords(team: List<Player>): Location {
        var teamX = 0.0;
        var teamY = 0.0;
        var teamZ = 0.0;
        val teamWorlds: MutableList<World> = mutableListOf()

        for (member: Player in team){
            teamX += member.location.x
            teamY += member.location.y
            teamZ += member.location.z
            teamWorlds.add(member.world)
        }
        val world = mostFrequentWorld(teamWorlds)

        return Location(world, (teamX/team.size).toInt().toDouble(),(teamY/team.size).toInt().toDouble(),(teamZ/team.size).toInt().toDouble())
    }

    private fun mostFrequentWorld(teamWorlds: MutableList<World>): World? {
        val numbersByElement = teamWorlds.groupingBy { it }.eachCount()
        return numbersByElement.maxByOrNull { it.value }?.key
    }

    //    public void showGlow(){
    //        for (Player player : main.runners)
    //            GlowAPI.setGlowing(player, GlowAPI.Color.GREEN, main.runners);
    //        for (Player player : main.hunters)
    //            GlowAPI.setGlowing(player, GlowAPI.Color.GREEN, main.hunters);
    //    }
    companion object {
        private val random = Random()
        fun generateSpawner() {
            val nether = Bukkit.getWorld("world_nether")
            val x = generateCoordinate()
            val y = random.nextDouble(54.0) + 36.0
            val z = generateCoordinate()
            val firstBlock = Location(nether, x, y, z)
            val block = nether!!.getBlockAt(firstBlock)
            block.type = Material.SPAWNER
            val blockState = block.state as CreatureSpawner
            blockState.spawnedType = EntityType.BLAZE
            generatePlatform(block, nether)
            Bukkit.broadcastMessage("Spawner generated at: ${x.roundToInt()}, ${y.roundToInt()}, ${z.roundToInt()}")
        }

        private fun generatePlatform(block: Block, nether: World) {
            val spawnerBlockLocation = block.location
            val locX = spawnerBlockLocation.x.toInt()
            val locY = spawnerBlockLocation.y.toInt() - 1
            val locZ = spawnerBlockLocation.z.toInt()

            // Make 4 quadrants around the block
            for (i in 0..7){
                for (j in 0..7){
                    nether.getBlockAt(locX + i, locY, locZ+j).type = Material.OBSIDIAN
                }
            }
            for (i in 0..7){
                for (j in 0..7){
                    nether.getBlockAt(locX - i, locY, locZ+j).type = Material.OBSIDIAN
                }
            }
            for (i in 0..7){
                for (j in 0..7){
                    nether.getBlockAt(locX - i, locY, locZ-j).type = Material.OBSIDIAN
                }
            }
            for (i in 0..7){
                for (j in 0..7){
                    nether.getBlockAt(locX + i, locY, locZ-j).type = Material.OBSIDIAN
                }
            }
        }

        private fun generateCoordinate(): Double {
            var axis = 1
            if (Math.random() < 0.5) {
                axis = -1
            }
            return random.nextDouble(300.0) * axis
        }
    }
}