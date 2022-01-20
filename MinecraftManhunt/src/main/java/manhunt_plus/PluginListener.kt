package manhunt_plus

import manhunt_plus.chest_generation.createChest
import manhunt_plus.game_state.AdvancementValue
import org.bukkit.*
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityDropItemEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.server.TabCompleteEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.potion.PotionEffectType
import java.util.*
import java.util.stream.Collectors

class PluginListener(var main: PluginMain) : Listener {
    private val random = Random()
    private var thrown = false
    private var spawnersGenerated = 0
    private var firstEntry = false

    /**
     * When a player clicks
     * If the item in hand is a compass, display the compass inventory to the player
     * If the item is an ender eye, update the game score.
     *
     * @param e the click event.
     */
    @EventHandler
    fun onClick(e: PlayerInteractEvent) {
        val player = e.player
        if (player.equipment?.itemInMainHand?.type == Material.COMPASS) {
            if (!main.playerIsOnTeam(player)) {
                if (player.isOp) {
                    player.sendMessage("Join a Manhunt team before using the compass!")
                }
                return
            }
            if (main.commands.compassTask == -1) {
                player.sendMessage("Start the Manhunt game before using the compass!")
                return
            }
            val inv = TargetSelectInventory(main)
            inv.displayToPlayer(player)
        } else if (player.equipment?.itemInMainHand?.type  == Material.ENDER_EYE) {
            if (main.runners.contains(player)) {
                if (e.action == Action.RIGHT_CLICK_AIR) {
                    if (!thrown) {
                        main.gameStateCalculator.updateAchievement(AdvancementValue.EYE_THROW)
                        thrown = true
                    }
                }
            }
        }
    }

    /**
     * When a player click in an inventory
     * Check if the item clicked is a player head in a compass
     *
     * @param event inventory click.
     */
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val hunter = event.whoClicked as Player
        val clickedHead = event.currentItem
        if (event.view.title == TargetSelectInventory.INVENTORY_NAME) {
            if (!main.playerIsOnTeam(hunter)) {
                hunter.sendMessage("You're not on a Manhunt team!")
                event.isCancelled = true
                return
            }
            if (clickedHead == null || clickedHead.type != Material.PLAYER_HEAD) {
                main.mainLogger.warning("Item clicked is not player head.")
                event.isCancelled = true
                return
            }
            if (!clickedHead.hasItemMeta()) {
                main.mainLogger.warning("Clicked head has no item meta.")
                hunter.sendMessage("Something went wrong: Does not have ItemMeta")
                event.isCancelled = true
                return
            }
            val itemMeta = clickedHead.itemMeta
            if (itemMeta !is SkullMeta) {
                main.mainLogger.warning("Clicked head meta is not instanceof SkullMeta.")
                main.mainLogger.info(itemMeta?.javaClass.toString())
                hunter.sendMessage("Something went wrong: Not an instanceof SkullMeta")
                event.isCancelled = true
                return
            }
            val target = itemMeta.owningPlayer
            var targetName = target!!.name
            if (targetName == null) {
                targetName = itemMeta.displayName
                main.mainLogger.info("Target name is null, applying offline mode workaround. Using item display name: $targetName")
            }
            main.targets[hunter.name] = targetName
            event.isCancelled = true
            hunter.closeInventory()
            hunter.sendMessage("Compass is now targeting $targetName")
        }
    }

    /**
     * When a player enters a portal
     * Update the portal location for the hunters' compass
     *
     * @param event Portal enter event
     */
    @EventHandler
    fun onPlayerEnterPortal(event: PlayerPortalEvent) {
        // If overworld to nether
        if (event.from.world?.equals(Bukkit.getWorld("world")) == true
            && event.to?.world?.equals(Bukkit.getWorld("world_nether")) == true){
            main.overworldPortals[event.player.name] = event.from
            main.netherPortals[event.player.name] = requireNotNull(event.to)
            //todo: Add timer for runner compass
            if (!firstEntry) {
                firstEntry = true
                Bukkit.getScheduler().scheduleSyncRepeatingTask(main, {
                    if (spawnersGenerated < 5) {
                        TaskManager.generateSpawner()
                        spawnersGenerated++
                    }
                }, 8400L, 3600L)
            }
        }
        // If overworld to end
        else if (event.from.world?.equals(Bukkit.getWorld("world")) == true
            && event.to?.world?.equals(Bukkit.getWorld("world_end")) == true){
            main.overworldPortals[event.player.name] = event.from
        }
    }

    //    @EventHandler
    //    public void onPlayerJoin(PlayerJoinEvent event) {
    //        if (main.getConfig().getBoolean("paused")){
    //            return;
    //        }
    //        if (!main.commands.worldBorderModified && main.getConfig().getBoolean("preGameWorldBorder", false)) {
    //            Location joinLoc = event.getPlayer().getLocation();
    //            WorldBorder wb = main.getWorld().getWorldBorder();
    //
    //            wb.setDamageAmount(0);
    //            wb.setWarningDistance(0);
    //            wb.setCenter(joinLoc);
    //            wb.setSize(main.getConfig().getInt("preGameBorderSize", 700));
    //
    //            main.commands.worldBorderModified = true;
    //        }
    //    }

    /**
     * When a player gets respawned
     * Give player items
     *
     * @param event respawn event
     */
    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        if (main.hunters.contains(event.player)) {
            respawnHunter(event.player)
        } else if (main.runners.contains(event.player)) {
            respawnRunner(event.player)
        }
    }

    /**
     * When a piglin drops an item
     * Drop extra items
     *
     * @param event entity item drop event
     */
    @EventHandler
    fun onPiglinTrade(event: EntityDropItemEvent) {
        if (event.entity.type == EntityType.PIGLIN) {
            //TODO: REFACTOR THESE METHODS INTO ONE METHOD
            if (random.nextInt(10) == 1) dropGold(event.entity)
            if (random.nextInt(15) == 1) dropFood(event.entity)
            if (random.nextInt(27) == 1) dropEnderPearl(event.entity)
            if (random.nextInt(20) == 1) spawnPiglin(event.entity)
            if (random.nextInt(40) == 1) dropBlazeRod(event.entity)
        }
    }

    private fun dropBlazeRod(entity: Entity) {
        entity.world.dropItem(entity.location, ItemStack(Material.BLAZE_ROD))
    }

    private fun spawnPiglin(entity: Entity) {
        entity.world.spawnEntity(entity.location, EntityType.PIGLIN)
    }

    private fun dropEnderPearl(entity: Entity) {
        entity.world.dropItem(entity.location, ItemStack(Material.ENDER_PEARL, random.nextInt(2) + 1))
    }

    private fun dropFood(entity: Entity) {
        entity.world.dropItem(entity.location, ItemStack(Material.GOLDEN_CARROT, random.nextInt(5) + 1))
    }

    private fun dropGold(entity: Entity) {
        entity.world.dropItem(entity.location, ItemStack(Material.GOLD_INGOT))
    }

    /**
     * When an entity dies (like an animal)
     * Cut clean if the animal drops food, or end game if the animal is the ender dragon
     *
     * @param event entity death event
     */
    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        if (main.commands.isCutClean) {
            // If animal that gives food is killed
            handleCutClean(event)
        }
        if (event.entity.type == EntityType.BLAZE) {
            handleBlazeDeath(event.entity)
        }

        // If ender dragon is killed
        if (event.entity.type == EntityType.ENDER_DRAGON) {
            for (player in main.hunters) {
                player.sendTitle(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "YOU LOSE!", "You're gay!", 20, 60, 20)
            }
            for (player in main.runners) {
                player.sendTitle(ChatColor.GREEN.toString() + ChatColor.BOLD + "YOU WIN!", ChatColor.MAGIC.toString() + "Now go have sex!", 20, 60, 20)
            }
            main.isGameIsOver = true
            main.commands.gameIsRunning = false
        }
    }

    private fun handleBlazeDeath(entity: Entity) {
        if (Math.random() < 0.5) {
            entity.world.dropItem(entity.location, ItemStack(Material.GOLD_INGOT))
        }
        if (Math.random() < 0.045) {
            entity.world.dropItem(entity.location, ItemStack(Material.ENDER_PEARL))
        }
        if (Math.random() < 0.01) {
            entity.world.dropItem(entity.location, ItemStack(Material.NETHERITE_SCRAP))
        }
    }

    private fun handleCutClean(event: EntityDeathEvent) {
        when (event.entity) {
            is Chicken -> {
                for (item in event.drops) {
                    if (item.type == Material.CHICKEN) {
                        item.type = Material.COOKED_CHICKEN
                    }
                }
            }
            is Cow -> {
                for (drop in event.drops) {
                    if (drop.type == Material.BEEF) {
                        drop.type = Material.COOKED_BEEF
                    }
                }
            }
            is Pig, is Hoglin -> {
                for (item in event.drops) {
                    if (item.type == Material.PORKCHOP) {
                        item.type = Material.COOKED_PORKCHOP
                    }
                }
            }
            is Rabbit -> {
                for (item in event.drops) {
                    if (item.type == Material.RABBIT) {
                        item.type = Material.COOKED_RABBIT
                    }
                }
            }
            is Sheep -> {
                for (item in event.drops) {
                    if (item.type == Material.MUTTON) {
                        item.type = Material.COOKED_MUTTON
                    }
                }
            }
        }
    }

    /**
     * When a player dies
     * Update game state if hunter, give runners extra items if killer, update death counts
     *
     * @param event player death event
     */
    @Suppress("DEPRECATION")
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (main.commands.gameIsRunning) {
            // If hunter is killed
            if (main.hunters.contains(event.entity)) {
                event.drops.removeIf { i: ItemStack -> i.type == Material.COMPASS }
                if (main.runners.contains((event.entity.killer)?.player)) { // If runner is killer
                    val killer = Bukkit.getPlayer(event.entity.killer!!.name)
                    main.hunterDeaths[event.entity.player as Player] = main.hunterDeaths[event.entity.player]!! + 1 // Increases death total for hunter
                    if (main.commands.runnerHelp) {
                        killer?.player?.maxHealth = event.entity.killer?.maxHealth?.plus(main.health/10)!!
                        killer?.player?.healthScale = event.entity.killer!!.healthScale + main.health/10
                    }
                    if (main.commands.extraDrops) {
                        giveRandomDrop(killer)
                    }
                    killer?.addPotionEffect(PotionEffectType.REGENERATION.createEffect(150,1))
                    main.gameStateCalculator.updateDeaths()
                    Bukkit.broadcastMessage("Game state is now: " + main.gameState)
                }
            }
            // If runner is killed
            if (main.runners.contains(event.entity)) {
                if (main.hunters.contains(event.entity.killer)) { // If hunter is killer
                    main.runnerDeaths[event.entity.player as Player] = main.runnerDeaths[event.entity.player]!! + 1 // Increases death total for runner
                    val deathCounts: Collection<Int> = main.runnerDeaths.values
                    if (!deathCounts.contains(0)) {
                        main.isGameIsOver = true
                        main.commands.gameIsRunning = false
                        Bukkit.broadcastMessage("Game is over, all runner's died!")
                    }
                }
            }
        } else {
            main.mainLogger.info("Game isn't running")
        }
    }

    /**
     * When a player gets an advancement.
     * Update the gameState if correct advancement.
     *
     * @param event advancement done event
     */
    @EventHandler
    fun onAdvancement(event: PlayerAdvancementDoneEvent) {
        if (main.runners.contains(event.player)) {
            val achievementName = event.advancement.key.key
            val enumName = keyToEnumName(achievementName)
            val matchList = Arrays.stream(AdvancementValue.values()).filter { advancementValue: AdvancementValue -> advancementValue.toString() == enumName }.collect(Collectors.toList())
            if (matchList.size != 0) {
                val match = matchList[0]
                main.gameStateCalculator.updateAchievement(match)
            }
        }
    }

    /**
     * Turn an advancement key into the string for an AdvancementValue
     *
     * @param key string to be changed
     * @return changed string
     */
    private fun keyToEnumName(key: String): String {
        val lastSlash = key.lastIndexOf('/')
        return key.substring(lastSlash + 1).uppercase(Locale.getDefault())
    }

    /**
     * When a block breaks
     * Handle cut clean and generate chest
     *
     * @param event
     */
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
//        if (main.getConfig().getBoolean("paused")){
//            event.getPlayer().sendMessage("Game is paused, wait until the game starts until you break blocks");
//            event.setCancelled(true);
//        }
        // If chest generate is on
        if (main.commands.chestGenerate) { // THIS MUST BE BEFORE CUT CLEAN CHECK
            var numberGenerated = 0
            if (main.getTeam(event.player) == main.hunters)
                numberGenerated = random.nextInt(625) // 625
            else if (main.getTeam(event.player) == main.runners)
                numberGenerated = random.nextInt(525) // 525

            if (numberGenerated == 69) { // 69
                val blockBrokenLocation = event.block.location
                createChest(blockBrokenLocation, event, event.player.world)
            }
        }
        if (main.commands.isCutClean) {
            // If player is a hunter, run probability for no cut clean
            if (main.getTeam(event.player) == main.hunters){
                if (cutCleanCalculator()){
                    return
                }
            }
            // Handle cut clean
            val blockBroken = event.block
            val world = blockBroken.world
            val location = blockBroken.location
            when (event.block.type) {
                //todo: Refactor this code
                Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE -> {
                    event.isCancelled = true
                    blockBroken.type = Material.AIR
                    world.dropItemNaturally(location, ItemStack(Material.IRON_INGOT))
                    val orb = world.spawn(location, ExperienceOrb::class.java)
                    orb.experience = 2
                }
                Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE -> {
                    event.isCancelled = true
                    blockBroken.type = Material.AIR
                    world.dropItemNaturally(location, ItemStack(Material.GOLD_INGOT))
                    val orb = world.spawn(location, ExperienceOrb::class.java)
                    orb.experience = 2
                }
                Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE -> {
                    event.isCancelled = true
                    blockBroken.type = Material.AIR
                    world.dropItemNaturally(location, ItemStack(Material.COPPER_INGOT))
                    val orb = world.spawn(location, ExperienceOrb::class.java)
                    orb.experience = 2
                }
                Material.POTATOES -> {
                    event.isCancelled = true
                    blockBroken.type = Material.AIR
                    world.dropItemNaturally(location, ItemStack(Material.BAKED_POTATO))
                    val orb = world.spawn(location, ExperienceOrb::class.java)
                    orb.experience = 1
                }
                else -> return
            }
        }

    }

    private fun cutCleanCalculator(): Boolean {
        return if(main.gameState < 3.7){
            Math.random() < 0.63
        } else if (main.gameState < 5){
            Math.random() < 0.3
        } else{
            Math.random() < 0.2
        }
    }


    /**
     * Give player a random drop
     *
     * @param player the player receiving the drop
     */
    //todo: Make and inventory randomizer
    private fun giveRandomDrop(player: Player?) {
        when (random.nextInt(30)) {
            0, 1, 21, 22 -> player?.player?.inventory?.addItem(ItemStack(Material.GOLDEN_APPLE))
            2, 3, 23, 24 -> {
                player!!.player!!.inventory.addItem(ItemStack(Material.DIAMOND, random.nextInt(3) + 1))
            }
            4, 5, 6, 25, 26 -> {
                player?.player?.inventory?.addItem(ItemStack(Material.ENDER_PEARL, random.nextInt(3) + 1))
            }
            7, 8, 27, 28 -> player?.player?.inventory?.addItem(ItemStack(Material.ENDER_EYE))
            9, 10, 11 -> {
                player?.player?.inventory?.addItem(ItemStack(Material.BREAD, 32))
            }
            13, 14, 15, 16 -> {
                player?.player?.inventory?.addItem(ItemStack(Material.OAK_WOOD, 16))
            }
            17, 18, 19 -> {
                player?.player?.inventory?.addItem(ItemStack(Material.IRON_INGOT, random.nextInt(5 + 1)))
            }
            20, 12 -> player?.player?.inventory?.addItem(ItemStack(Material.ENCHANTING_TABLE))
            else -> player?.player?.inventory?.addItem(ItemStack(Material.CYAN_BED))
        }
    }

    @Suppress("DEPRECATION")
    private fun respawnRunner(player: Player) {
        Bukkit.getServer().scheduler.scheduleSyncDelayedTask(main, {
            val runnerName = player.name
            if (main.runnerDeaths[player] != 0) {
                player.sendMessage("You are eliminated. Keep your teammate alive")
                Bukkit.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.RED + runnerName + " IS ELIMINATED.")
                Bukkit.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.RED + runnerName + " ELIMINATE REMAINING RUNNERS TO WIN!")
                Bukkit.broadcastMessage(ChatColor.AQUA.toString() + runnerName + " is still able to participate. Help your teammate win!")
                player.sendTitle(ChatColor.DARK_RED.toString() + "ELIMINATED", ChatColor.AQUA.toString() + "Help the runner team win!", 20, 60, 20)
            } else {
                player.sendMessage("You're still in the game! Get to your teammate quickly before the hunters eliminate you!")
            }
            val speedTime: Int = if (player.bedSpawnLocation!= null){
                 240
            } else {
                4800
            }
            player.addPotionEffect(PotionEffectType.SPEED.createEffect(speedTime, 2))
            player.healthScale = main.health
            player.maxHealth = main.health
            player.health = main.health
            if (main.commands.runnerHelp) {
                respawnItems(player)
            }
        }, 60L)
    }

    private fun respawnHunter(player: Player) {
        Bukkit.getServer().scheduler.scheduleSyncDelayedTask(main, {
            player.sendMessage("Death total: " + main.hunterDeaths[player])
            player.player?.inventory?.addItem(ItemStack(Material.COMPASS, 1))
            val speedTime: Int = if (player.bedSpawnLocation == null) {
                if (main.gameState < 3) {
                    1200
                } else {
                    4800
                }
            } else {
                200
            }
            player.player?.addPotionEffect(PotionEffectType.SPEED.createEffect(speedTime, 2))
            if (main.commands.hunterHelp) {
                respawnItems(player)
            }
        }, 60L)
    }

    private fun respawnItems(respawned: Player?) {
        val inventoryToAdd = respawned?.player?.inventory
        val itemsToAdd = main.itemGenerator.generateInventory()
        for (itemStack in itemsToAdd) {
            inventoryToAdd?.addItem(itemStack)
        }
    }

    @EventHandler
    fun onAutocomplete(event: TabCompleteEvent) {
        val buffer = event.buffer
        if (!buffer.startsWith("/")) return
        val args: Array<String?> = buffer.split(" ").toTypedArray()
        val completions = main.commands.getCompletions(args, event.completions)
        event.completions = completions
    }
}