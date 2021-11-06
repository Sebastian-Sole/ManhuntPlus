package com.yoonicode.minecraftmanhuntplus;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.bukkit.Bukkit.getServer;

public class PluginListener implements Listener {

    boolean setRunnersToSpecOnDeath;
    private Random random = new Random();

    PluginMain main;
    public PluginListener(PluginMain main) {
        this.main = main;
        setRunnersToSpecOnDeath = main.getConfig().getBoolean("setRunnersToSpecOnDeath", true);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e){
        Player player = e.getPlayer();

        if(player.getEquipment().getItemInMainHand().getType() == Material.COMPASS){
            if(!main.playerIsOnTeam(player)){
                if(player.isOp()){
                    player.sendMessage("Join a Manhunt team before using the compass!");
                }
                return;
            }
            if(main.runners.contains(player.getName()) && !main.debugMode){
                player.sendMessage("Speedrunners cannot use the compass!");
                return;
            }
            if(main.commands.compassTask == -1){
                player.sendMessage("Start the Manhunt game before using the compass!");
                return;
            }
            TargetSelectInventory inv = new TargetSelectInventory(main);
            inv.DisplayToPlayer(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player hunter = (Player) event.getWhoClicked();

        ItemStack clickedHead = event.getCurrentItem();
        if (event.getView().getTitle().equals(TargetSelectInventory.INVENTORY_NAME)) {
            if(!main.playerIsOnTeam(hunter)){
                hunter.sendMessage("You're not on a Manhunt team!");
                event.setCancelled(true);
                return;
            }

            if(clickedHead == null || clickedHead.getType() != Material.PLAYER_HEAD){
                main.logger.warning("Item clicked is not player head.");
                event.setCancelled(true);
                return;
            }
            if(!clickedHead.hasItemMeta()) {
                main.logger.warning("Clicked head has no item meta.");
                hunter.sendMessage("Something went wrong: Does not have ItemMeta");
                event.setCancelled(true);
                return;
            }
            ItemMeta itemmeta = clickedHead.getItemMeta();
            if(!(itemmeta instanceof SkullMeta)){
                main.logger.warning("Clicked head meta is not instanceof SkullMeta.");
                main.logger.info(itemmeta.getClass().toString());
                hunter.sendMessage("Something went wrong: Not an instanceof SkullMeta");
                event.setCancelled(true);
                return;
            }
            SkullMeta meta = (SkullMeta)itemmeta;
            OfflinePlayer target = meta.getOwningPlayer();
            String targetName = target.getName();
            if(targetName == null){
                targetName = meta.getDisplayName();
                main.logger.info("Target name is null, applying offline mode workaround. Using item display name: " + targetName);
            }
            main.targets.put(hunter.getName(), targetName);
            event.setCancelled(true);
            hunter.closeInventory();
            hunter.sendMessage("Compass is now targeting " + targetName);
        }
    }

    @EventHandler
    public void onPlayerEnterPortal(PlayerPortalEvent event){
        main.portals.put(event.getPlayer().getName(), event.getFrom());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!main.commands.worldBorderModified && main.getConfig().getBoolean("preGameWorldBorder", false)) {
            Location joinLoc = event.getPlayer().getLocation();
            WorldBorder wb = main.world.getWorldBorder();

            wb.setDamageAmount(0);
            wb.setWarningDistance(0);
            wb.setCenter(joinLoc);
            wb.setSize(main.getConfig().getInt("preGameBorderSize", 700));

            main.commands.worldBorderModified = true;
        }
    }

//    @EventHandler
//    public void onPlayerHit(EntityDamageByEntityEvent event) {
//        if (!main.commands.gameIsRunning && main.getConfig().getBoolean("startGameByHit", false)) {
//            Entity victim = event.getEntity();
//            Entity attacker = event.getDamager();
//            EntityDamageEvent.DamageCause cause = event.getCause();
//            if (attacker.getType() == EntityType.PLAYER && victim.getType() == EntityType.PLAYER
//                    && cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK
//                    && main.huntersTeam.getEntries().contains(victim.getName())
//                    && main.runnersTeam.getEntries().contains(attacker.getName())) {
//                main.commands.hitHasRegistered = true;
//                attacker.getServer().dispatchCommand(getServer().getConsoleSender(), "start");
//            }
//        }
//    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        if(main.commands.gameIsRunning){
            if (main.hunters.contains(event.getPlayer().getName())) {
                respawnHunter(event.getPlayer());
            }
            else if (main.runners.contains(event.getPlayer().getName())) {
                respawnRunner(event.getPlayer());
            }
        }
        else{
            main.logger.info("Game isn't running");
        }
//        if(setRunnersToSpecOnDeath && main.commands.gameIsRunning && main.runners.contains(event.getPlayer().getName())){
//            BukkitScheduler scheduler = Bukkit.getScheduler();
//            scheduler.scheduleSyncDelayedTask(main, new Runnable() {
//                @Override
//                public void run() {
//                    main.logger.info("Setting player to gamemode spectator");
//                    event.getPlayer().setGameMode(GameMode.SPECTATOR);
//                }
//            }, 20L * 1); // Wait a bit before setting to spectator to prevent a race condition
//        }
    }


    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        Entity entity = event.getEntity();
        if (entity.getType().equals(EntityType.ENDER_DRAGON)){
            for (String string : main.hunters){
                Bukkit.getPlayer(string).sendTitle(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "YOU LOSE!", "You're gay!", 20, 60, 20);
            }
            for (String string : main.runners){
                Bukkit.getPlayer(string).sendTitle(ChatColor.GREEN.toString() + ChatColor.BOLD + "YOU WIN!", ChatColor.MAGIC + "Now go have sex!", 20, 60, 20);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) throws InterruptedException {
        if (main.commands.gameIsRunning) {
            // If hunter is killed
            if (main.hunters.contains(event.getEntity().getName())) {
                event.getDrops().removeIf(i -> i.getType() == Material.COMPASS);
                if (main.runners.contains(event.getEntity().getKiller().getPlayer().getName())) { // If runner is killer
                    Player killer = Bukkit.getPlayer(event.getEntity().getKiller().getName());
                    main.hunterDeaths.put(event.getEntity().getPlayer().getName(), main.hunterDeaths.get(event.getEntity().getPlayer().getName()) + 1); // Increases death total for hunter
                    if (main.commands.runnerHelp) {
                        killer.getPlayer().setMaxHealth(event.getEntity().getKiller().getMaxHealth() + 2.0);
                        killer.getPlayer().setHealthScale((event.getEntity().getKiller().getHealthScale() + 2.0));
                    }
                    if (main.commands.extraDrops) {
                        giveRandomDrop(killer);
                    }
                }
            }
            // If runner is killed
            if (main.runners.contains(event.getEntity().getName())){
                if (main.hunters.contains(event.getEntity().getKiller().getName())){ // If hunter is killer
                    main.runnerDeaths.put(event.getEntity().getPlayer().getName(), main.runnerDeaths.get(event.getEntity().getPlayer().getName())+1); // Increases death total for runner
                }
            }
        }
        else{
            main.logger.info("Game isn't running");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if (main.commands.chestGenerate) {
            Location blockBrokenLocation = event.getBlock().getLocation();
            int numberGenerated = this.random.nextInt(750);
            if (numberGenerated == 69) {
                createChest(blockBrokenLocation, event);
            }
        }
    }

    private void createChest(Location location, BlockBreakEvent event) {
        Block block = main.getWorld().getBlockAt(location);
        block.setType(Material.CHEST);
        event.setCancelled(true);
        Chest chest = (Chest) block.getState();
        Inventory inv = chest.getInventory();
        var itemsList = generateChestItems();
        for (ItemStack stack : itemsList){
            inv.addItem(stack);
        }
    }

    private ArrayList<ItemStack> generateChestItems() {
        ArrayList<ItemStack> itemsToAddToChest = new ArrayList<>();
        int numberOfItemsToAdd = random.nextInt(8)+1;
        for (int i = 0; i < numberOfItemsToAdd; i++){ //Generate that amount of random items
            int generatedNumber = random.nextInt(164)+1;
            int enchantmentType = random.nextInt(3)+1;
            int bookEnchantmentType = random.nextInt(10)+1;
            boolean shouldEnchant = false;
            int enchantLevel = random.nextInt(3);
            if (enchantLevel != 0) {
                shouldEnchant = true;
            }
            switch (generatedNumber){
                // Diamond Boots
                case 1,2  -> {
                    var item = new ItemStack(Material.DIAMOND_HELMET);
                    if (shouldEnchant) {
                        switch (enchantmentType) {
                            case 1 -> item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchantLevel);
                            case 2 -> item.addEnchantment(Enchantment.PROTECTION_FIRE, enchantLevel);
                            case 3 -> item.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, enchantLevel);
                            case 4 -> item.addEnchantment(Enchantment.OXYGEN, enchantLevel);
                        }
                    }
                    itemsToAddToChest.add(item);
                }
                // Diamond Leggings
                case 3 -> {
                    var item = new ItemStack(Material.DIAMOND_LEGGINGS);
                    if (shouldEnchant) {
                        switch (enchantmentType) {
                            case 1 -> item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchantLevel);
                            case 2 -> item.addEnchantment(Enchantment.PROTECTION_FIRE, enchantLevel);
                            case 3 -> item.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, enchantLevel);
                            case 4 -> item.addEnchantment(Enchantment.PROTECTION_PROJECTILE, enchantLevel);
                        }
                    }
                    itemsToAddToChest.add(item);
                }
                // Diamond Chestplate
                case 5 -> {
                    var item = new ItemStack(Material.DIAMOND_CHESTPLATE);
                    if (shouldEnchant) {
                        switch (enchantmentType) {
                            case 1 -> item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchantLevel);
                            case 2 -> item.addEnchantment(Enchantment.PROTECTION_FIRE, enchantLevel);
                            case 3 -> item.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, enchantLevel);
                            case 4 -> item.addEnchantment(Enchantment.PROTECTION_PROJECTILE, enchantLevel);
                        }
                    }
                    itemsToAddToChest.add(item);
                }
                // Diamond Boots
                case 7,8 -> {
                    var item = new ItemStack(Material.DIAMOND_BOOTS);
                    if (shouldEnchant) {
                        switch (enchantmentType) {
                            case 1 -> item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchantLevel);
                            case 2 -> item.addEnchantment(Enchantment.SOUL_SPEED, enchantLevel);
                            case 3 -> item.addEnchantment(Enchantment.FROST_WALKER, enchantLevel);
                            case 4 -> item.addEnchantment(Enchantment.DEPTH_STRIDER, enchantLevel);
                        }
                    }
                    itemsToAddToChest.add(item);
                }
                // Iron Helmet
                case 9,10,11,77,128 -> {
                    var item = new ItemStack(Material.IRON_HELMET);
                    if (enchantLevel == 2){
                        enchantLevel--;
                    }
                    if (shouldEnchant) {
                        switch (enchantmentType) {
                            case 1 -> item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchantLevel + random.nextInt(2));
                            case 2 -> item.addEnchantment(Enchantment.PROTECTION_FIRE, enchantLevel + random.nextInt(2));
                            case 3 -> item.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, enchantLevel + random.nextInt(2));
                            case 4 -> item.addEnchantment(Enchantment.OXYGEN, enchantLevel + random.nextInt(2));
                        }
                    }
                    itemsToAddToChest.add(item);
                }
                // Iron Chestplate
                case 12,13,102,129 -> {
                    var item = new ItemStack(Material.IRON_CHESTPLATE);
                    if (enchantLevel == 2){
                        enchantLevel--;
                    }
                    if (shouldEnchant) {
                        switch (enchantmentType) {
                            case 1 -> item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchantLevel + random.nextInt(2));
                            case 2 -> item.addEnchantment(Enchantment.PROTECTION_FIRE, enchantLevel + random.nextInt(2));
                            case 3 -> item.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, enchantLevel + random.nextInt(2));
                            case 4 -> item.addEnchantment(Enchantment.PROTECTION_PROJECTILE, enchantLevel + random.nextInt(2));
                        }
                    }
                    itemsToAddToChest.add(item);
                }
                // Iron Leggings
                case 14,15,101,130 -> {
                    var item = new ItemStack(Material.IRON_LEGGINGS);
                    if (enchantLevel == 2){
                        enchantLevel--;
                    }
                    if (shouldEnchant) {
                        switch (enchantmentType) {
                            case 1 -> item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchantLevel + random.nextInt(2));
                            case 2 -> item.addEnchantment(Enchantment.PROTECTION_FIRE, enchantLevel + random.nextInt(2));
                            case 3 -> item.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, enchantLevel + random.nextInt(2));
                            case 4 -> item.addEnchantment(Enchantment.PROTECTION_PROJECTILE, enchantLevel + random.nextInt(2));
                        }
                    }
                    itemsToAddToChest.add(item);
                }
                // Iron Boots
                case 16,17,18,78,131 -> {
                    var item = new ItemStack(Material.IRON_CHESTPLATE);
                    if (enchantLevel == 2){
                        enchantLevel--;
                    }
                    if (shouldEnchant) {
                        switch (enchantmentType) {
                            case 1 -> item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchantLevel + random.nextInt(2));
                            case 2 -> item.addEnchantment(Enchantment.FROST_WALKER, enchantLevel + random.nextInt(2));
                            case 3 -> item.addEnchantment(Enchantment.SOUL_SPEED, enchantLevel + random.nextInt(2));
                            case 4 -> item.addEnchantment(Enchantment.DEPTH_STRIDER, enchantLevel + random.nextInt(2));
                        }
                    }
                    itemsToAddToChest.add(item);
                }
                // Diamond Pickaxe
                case 19,20,132,133 -> {
                    var item = new ItemStack(Material.DIAMOND_PICKAXE);
                    if (shouldEnchant) {
                        switch (enchantmentType) {
                            case 1 -> item.addEnchantment(Enchantment.DIG_SPEED, enchantLevel);
                            case 2 -> item.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, enchantLevel);
                            case 3 -> item.addEnchantment(Enchantment.MENDING, 1);
                            case 4 -> item.addEnchantment(Enchantment.DURABILITY, enchantLevel);
                        }
                    }
                    itemsToAddToChest.add(item);
                }
                // Diamond Sword
                case 21,22,134 -> {
                    var item = new ItemStack(Material.DIAMOND_PICKAXE);
                    if (shouldEnchant) {
                        if (enchantmentType == 1 || enchantmentType == 2) {
                            item.addEnchantment(Enchantment.DIG_SPEED, 1);
                        }
                    }
                    itemsToAddToChest.add(item);
                }
                // Golden Apples
                case 23,24,25,79,103,135,136-> {
                    var item = new ItemStack(Material.GOLDEN_APPLE, enchantmentType);
                    itemsToAddToChest.add(item);
                }
                // Ender Pearl
                case 26,27,28,80,81,104,105,137 -> {
                    var item = new ItemStack(Material.ENDER_PEARL);
                    itemsToAddToChest.add(item);
                }
                // Diamonds
                case 29,30,31,138,139,140 -> {
                    int numGen = random.nextInt(3)+1;
                    var item = new ItemStack(Material.DIAMOND, numGen);
                    itemsToAddToChest.add(item);
                }
                // Iron
                case 32,33,34,35,84,106,141 -> {
                    int numGen = random.nextInt(9)+1;
                    var item = new ItemStack(Material.IRON_INGOT, numGen);
                    itemsToAddToChest.add(item);
                }
                // Coal
                case 36,37,38,39,86,87,107,108,142,143 -> {
                    int numGen = random.nextInt(15)+1;
                    var item = new ItemStack(Material.COAL, numGen);
                    itemsToAddToChest.add(item);
                }
                // Wood
                case 40,41,42,43,88,89,109,110,144,147,148 -> {
                    int numGen = random.nextInt(32)+1;
                    var item = new ItemStack(Material.OAK_WOOD, numGen);
                    itemsToAddToChest.add(item);
                }
                // Steak
                case 44,45,46,47,90,91,111,112,113,145,146 -> {
                    int numGen = random.nextInt(15)+1;
                    var item = new ItemStack(Material.COOKED_BEEF, numGen);
                    itemsToAddToChest.add(item);
                }
                // Anvil
                case 48,49 -> {
                    var item = new ItemStack(Material.ANVIL);
                    itemsToAddToChest.add(item);
                }
                // Enchant Table
                case 50,51 -> {
                    var item = new ItemStack(Material.ENCHANTING_TABLE);
                    itemsToAddToChest.add(item);
                }
                // Totem of Undying
                case 52 -> {
                    var item = new ItemStack(Material.TOTEM_OF_UNDYING);
                    itemsToAddToChest.add(item);
                }
                // Bottle of Enchantment
                case 53,54,55,92,93,114,115,116,149 -> {
                    var item = new ItemStack(Material.EXPERIENCE_BOTTLE, 10);
                    itemsToAddToChest.add(item);
                }
                // Gold Ingots
                case 56,57,58,59,94,95,150,151,152,153 -> {
                    int numGen = random.nextInt(15)+1;
                    var item = new ItemStack(Material.GOLD_INGOT, numGen);
                    itemsToAddToChest.add(item);
                }
                // Diamond Ore
                case 60,61,62,63,64 -> {
                    int numGen = random.nextInt(2)+1;
                    var item = new ItemStack(Material.DIAMOND_ORE, numGen);
                    itemsToAddToChest.add(item);
                }
                // Torches
                case 65,66,67,68,96,97,117,118,119,154,155,156 -> {
                    int numGen = random.nextInt(15)+1;
                    var item = new ItemStack(Material.TORCH, numGen);
                    itemsToAddToChest.add(item);
                }
                // Shulker Box
                case 69,124 -> {
                    var item = new ItemStack(Material.SHULKER_BOX);
                    itemsToAddToChest.add(item);
                }
                // Hay Block
                case 70,71,72,73,98,99,120,121,122,123,157,158,159 -> {
                    int numGen = random.nextInt(15)+1;
                    var item = new ItemStack(Material.HAY_BLOCK, numGen);
                    itemsToAddToChest.add(item);
                }
                // Enchanted Book
                case 74,75,76,100,125 -> {
                    var item = new ItemStack(Material.ENCHANTED_BOOK);
                    if (shouldEnchant) {
                        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                        switch (bookEnchantmentType) {
                            case 1 -> {
                                meta.addStoredEnchant(Enchantment.DAMAGE_ALL, 1, true);
                                item.setItemMeta(meta);
                            }
                            case 2 -> {
                                meta.addStoredEnchant(Enchantment.DEPTH_STRIDER, 1, true);
                                item.setItemMeta(meta);
                            }
                            case 3 -> {
                                meta.addStoredEnchant(Enchantment.DURABILITY, enchantLevel, true);
                                item.setItemMeta(meta);
                            }
                            case 4 -> {
                                meta.addStoredEnchant(Enchantment.PROTECTION_FALL, enchantLevel, true);
                                item.setItemMeta(meta);
                            }
                            case 5 -> {
                                meta.addStoredEnchant(Enchantment.ARROW_DAMAGE, 1, true);
                                item.setItemMeta(meta);
                            }
                            case 6 -> {
                                meta.addStoredEnchant(Enchantment.ARROW_KNOCKBACK, enchantLevel, true);
                                item.setItemMeta(meta);
                            }
                            case 7 -> {
                                meta.addStoredEnchant(Enchantment.DIG_SPEED, enchantLevel, true);
                                item.setItemMeta(meta);
                            }
                            case 8 -> {
                                meta.addStoredEnchant(Enchantment.WATER_WORKER, enchantLevel, true);
                                item.setItemMeta(meta);
                            }
                            case 9 -> {
                                meta.addStoredEnchant(Enchantment.LOOT_BONUS_BLOCKS, enchantLevel, true);
                                item.setItemMeta(meta);
                            }
                            case 10 -> {
                                meta.addStoredEnchant(Enchantment.FROST_WALKER, 1, true);
                                item.setItemMeta(meta);
                            }
                        }
                    }
                    itemsToAddToChest.add(item);
                }
                default -> itemsToAddToChest.add(new ItemStack(Material.WHEAT_SEEDS, 42));
            }
        }
        return itemsToAddToChest;
    }




    private void giveRandomDrop(Player player) {
        int generated = random.nextInt(35);
        switch (generated){
            case 0, 1 -> player.getPlayer().getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
            case 2, 3 -> {
                var diamondGenerated = random.nextInt(3)+1;
                for (int i =0; i<=diamondGenerated; i++){
                    player.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND));
                }
            }
            case 4, 5, 6 -> {
                var enderGen = random.nextInt(3)+1;
                for (int i = 0; i<=enderGen; i++){
                    player.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                }
            }
            case 7, 8 -> player.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_EYE));
            case 9, 10, 11,12 -> {
                for (int i = 0; i<=31; i++) {
                    player.getPlayer().getInventory().addItem(new ItemStack(Material.BREAD));
                }
            }
            case 13, 14, 15, 16 -> {
                for (int i =0; i<=15; i++){
                    player.getPlayer().getInventory().addItem(new ItemStack(Material.OAK_WOOD));
                }
            }
            case 17, 18,19 -> {
                int ironGenerated = random.nextInt(5+1);
                for (int i = 0; i<= ironGenerated; i++){
                    player.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_INGOT));
                }
            }
            case 20, 21, 22, 23 -> {
                int coalGenerated = random.nextInt(16)+1;
                for (int i = 0; i<=coalGenerated; i++){
                    player.getPlayer().getInventory().addItem(new ItemStack(Material.COAL));
                }
            }
            case 24, 25, 26,27,28 -> {
                for (int i =0; i<=63; i++){
                    player.getPlayer().getInventory().addItem(new ItemStack(Material.COBBLESTONE));
                }
            }
            case 29, 30, 31, 32, 33 -> {
                for (int i =0; i<= 63; i++){
                    player.getPlayer().getInventory().addItem(new ItemStack(Material.WHEAT_SEEDS));
                }
            }
            case 34 -> player.getPlayer().getInventory().addItem(new ItemStack(Material.ENCHANTING_TABLE));
            default -> player.getPlayer().getInventory().addItem(new ItemStack(Material.CYAN_BED));
        }
    }

    private void respawnRunner(Player player) {
        final String runnerName = player.getName();
        int giveKitID = getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
            public void run() {
                Player player = getServer().getPlayer(runnerName);
                if (main.runnerDeaths.get(runnerName) != 0) {
                    player.sendMessage("You are eliminated. Keep your teammate alive");
                    Bukkit.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.RED + runnerName + " IS ELIMINATED. ELIMINATE REMAINING RUNNERS TO WIN!");
                    Bukkit.broadcastMessage(ChatColor.AQUA + runnerName + " is still able to participate. Help your teammate win!");
                    player.sendTitle(ChatColor.DARK_RED + "ELIMINATED",ChatColor.AQUA + "Help the runner team win!", 20, 60,20);
                }
                else{
                    player.sendMessage("You're still in the game! Get to your teammate quickly before the hunters eliminate you!");
                }
                player.getPlayer().addPotionEffect(PotionEffectType.SPEED.createEffect(2400, 2));
                player.getPlayer().addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(2400, 2));
                player.setHealthScale(20.0);
                player.setMaxHealth(20.0);
                player.setHealth(20.0);
            }
        }, 20L);
    }

    private void respawnHunter(Player player) {
        final String hunterName = player.getName();
        int giveKitID = getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
            public void run() {
                Player player = getServer().getPlayer(hunterName);
                player.sendMessage("Death total: " + main.hunterDeaths.get(player.getName()));
                player.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS, 1));
                player.getPlayer().addPotionEffect(PotionEffectType.SPEED.createEffect(600, 2));
                player.getPlayer().addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(1200, 2));
                if (main.commands.hunterHelp) {
                    giveHunterItemsOnDeath(player);
                }
            }
        }, 20L);
    }

    private void giveHunterItemsOnDeath(Player respawned) {
        switch (main.hunterDeaths.get(respawned.getPlayer().getName())){
            case 1 -> {
                hunterRespawnOne(respawned);
            }
            case 2 -> {
                hunterRespawnTwo(respawned);
            }
            case 3 -> {
                hunterRespawnThree(respawned);
            }
            case 4 -> {
                hunterRespawnFour(respawned);
            }
            default -> hunterRespawnMore(respawned);
        }
    }

    private void hunterRespawnMore(Player respawned) {
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.STONE_AXE));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.STONE_SWORD));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_SHOVEL));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_HELMET));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_BOOTS));
        for (int i = 0; i<=10; i++){
            respawned.getPlayer().getInventory().addItem(new ItemStack(Material.COOKED_BEEF));
            respawned.getPlayer().getInventory().addItem(new ItemStack(Material.COAL));
            respawned.getPlayer().getInventory().addItem(new ItemStack(Material.OAK_WOOD));
        }
    }

    private void hunterRespawnFour(Player respawned) {
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.STONE_AXE));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.STONE_SWORD));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_SHOVEL));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_HELMET));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.GOLDEN_CHESTPLATE));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.GOLDEN_LEGGINGS));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.CHAINMAIL_BOOTS));
        for (int i = 0; i<=10; i++){
            respawned.getPlayer().getInventory().addItem(new ItemStack(Material.COOKED_BEEF));
            respawned.getPlayer().getInventory().addItem(new ItemStack(Material.COAL));
            respawned.getPlayer().getInventory().addItem(new ItemStack(Material.OAK_WOOD));
        }
    }

    private void hunterRespawnThree(Player respawned) {
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.STONE_AXE));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.STONE_SWORD));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_SHOVEL));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.GOLDEN_HELMET));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.LEATHER_CHESTPLATE));
        for (int i = 0; i<=10; i++){
            respawned.getPlayer().getInventory().addItem(new ItemStack(Material.COOKED_BEEF));
            respawned.getPlayer().getInventory().addItem(new ItemStack(Material.COAL));
            respawned.getPlayer().getInventory().addItem(new ItemStack(Material.OAK_WOOD));
        }
    }

    private void hunterRespawnTwo(Player respawned) {
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.STONE_AXE));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.STONE_SWORD));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.STONE_SHOVEL));
        for (int i = 0; i<=6; i++){
            respawned.getPlayer().getInventory().addItem(new ItemStack(Material.BREAD));
            respawned.getPlayer().getInventory().addItem(new ItemStack(Material.COAL));
            respawned.getPlayer().getInventory().addItem(new ItemStack(Material.OAK_WOOD));
        }
    }

    private void hunterRespawnOne(Player respawned) {
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
        respawned.getPlayer().getInventory().addItem(new ItemStack(Material.WOODEN_SHOVEL));
        for (int i = 0; i<=5; i++){
            respawned.getPlayer().getInventory().addItem(new ItemStack(Material.BREAD));
            respawned.getPlayer().getInventory().addItem(new ItemStack(Material.COAL));
        }
    }

    @EventHandler
    public void onAutocomplete(TabCompleteEvent event){
        String buffer = event.getBuffer();
        if(!buffer.startsWith("/")) return;
        String[] args = buffer.split(" ");

        List<String> completions = main.commands.getCompletions(args, event.getCompletions());

        event.setCompletions(completions);
    }

}
