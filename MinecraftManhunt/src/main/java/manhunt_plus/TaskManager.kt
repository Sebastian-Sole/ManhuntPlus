package manhunt_plus

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.CreatureSpawner
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.CompassMeta
import org.bukkit.potion.PotionEffectType
import java.util.*
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
            if (hunter.world.environment != target.world.environment) {
                val loc = main.portals[target.name]
                if (loc != null) {
                    hunter.compassTarget = loc
                }
                for (j in 0 until inv.size) {
                    val stack = inv.getItem(j) ?: continue
                    if (stack.type != Material.COMPASS) continue
                    stack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1) // Make all compasses glow
                    val meta = stack.itemMeta
                    meta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                    stack.itemMeta = meta
                }
            } else {
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
        TODO("Not yet implemented")
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