package com.yoonicode.minecraftmanhuntplus;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.Random;

import static org.bukkit.Bukkit.getServer;

public class PluginListener implements Listener {

    boolean setRunnersToSpecOnDeath;


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

    private void giveRandomDrop(Player player) {
        Random rand = new Random();
        int generated = rand.nextInt(35);
        switch (generated){
            case 0, 1 -> player.getPlayer().getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
            case 2, 3 -> {
                var diamondGenerated = rand.nextInt(3)+1;
                for (int i =0; i<=diamondGenerated; i++){
                    player.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND));
                }
            }
            case 4, 5, 6 -> {
                var enderGen = rand.nextInt(3)+1;
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
                int ironGenerated = rand.nextInt(5+1);
                for (int i = 0; i<= ironGenerated; i++){
                    player.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_INGOT));
                }
            }
            case 20, 21, 22, 23 -> {
                int coalGenerated = rand.nextInt(16)+1;
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
