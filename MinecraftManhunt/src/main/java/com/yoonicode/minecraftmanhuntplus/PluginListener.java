package com.yoonicode.minecraftmanhuntplus;

import com.yoonicode.minecraftmanhuntplus.chest_generation.ChestRandomizer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffectType;

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
            if(main.runners.contains(player) && !main.debugMode){
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
            WorldBorder wb = main.getWorld().getWorldBorder();

            wb.setDamageAmount(0);
            wb.setWarningDistance(0);
            wb.setCenter(joinLoc);
            wb.setSize(main.getConfig().getInt("preGameBorderSize", 700));

            main.commands.worldBorderModified = true;
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        if(main.commands.gameIsRunning){
            if (main.hunters.contains(event.getPlayer())) {
                respawnHunter(event.getPlayer());
            }
            else if (main.runners.contains(event.getPlayer())) {
                respawnRunner(event.getPlayer());
            }
        }
        else{
            main.logger.info("Game isn't running");
        }
    }

    @EventHandler
    public void onPiglinTrade(EntityDropItemEvent event){
        if (event.getEntity().equals(EntityType.PIGLIN)) {
            if (random.nextInt(10) == 1)
                dropGold(event.getEntity());
            if (random.nextInt(15) == 1)
                dropFood(event.getEntity());
            if (random.nextInt(27) == 1)
                dropEnderPearl(event.getEntity());
            if (random.nextInt(20) == 1)
                spawnPiglin(event.getEntity());
        }
    }

    private void spawnPiglin(Entity entity) {
        entity.getWorld().spawnEntity(entity.getLocation(),EntityType.PIGLIN);
    }

    private void dropEnderPearl(Entity entity) {
        entity.getWorld().dropItem(entity.getLocation(),new ItemStack(Material.ENDER_PEARL,random.nextInt(2)+1));
    }

    private void dropFood(Entity entity) {
        entity.getWorld().dropItem(entity.getLocation(),new ItemStack(Material.GOLDEN_CARROT,random.nextInt(5)+1));
    }

    private void dropGold(Entity entity) {
        entity.getWorld().dropItem(entity.getLocation(),new ItemStack(Material.GOLD_INGOT));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if (main.commands.isCutClean()) {
            // If animal that gives food is killed
            handleCutClean(event);
        }

        // If ender dragon is killed
        if (event.getEntity().getType().equals(EntityType.ENDER_DRAGON)){
            for (Player player : main.hunters){
                player.sendTitle(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "YOU LOSE!", "You're gay!", 20, 60, 20);
            }
            for (Player player : main.runners){
                player.sendTitle(ChatColor.GREEN.toString() + ChatColor.BOLD + "YOU WIN!", ChatColor.MAGIC + "Now go have sex!", 20, 60, 20);
            }
        }
    }

    private void handleCutClean(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Chicken) {
            for (ItemStack item : event.getDrops()) {
                if (item.getType().equals(Material.CHICKEN)) {
                    item.setType(Material.COOKED_CHICKEN);
                }
            }
        } else if (entity instanceof Cow) {
            for (ItemStack drop : event.getDrops()) {
                if (drop.getType().equals(Material.BEEF)) {
                    drop.setType(Material.COOKED_BEEF);
                }
            }
        } else if (entity instanceof Pig || entity instanceof Hoglin) {
            for (ItemStack item : event.getDrops()) {
                if (item.getType().equals(Material.PORKCHOP)) {
                    item.setType(Material.COOKED_PORKCHOP);
                }
            }
        } else if (entity instanceof Rabbit) {
            for (ItemStack item : event.getDrops()) {
                if (item.getType().equals(Material.RABBIT)) {
                    item.setType(Material.COOKED_RABBIT);
                }
            }
        } else if (entity instanceof Sheep) {
            for (ItemStack item : event.getDrops()) {
                if (item.getType().equals(Material.MUTTON)) {
                    item.setType(Material.COOKED_MUTTON);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)  {
        if (main.commands.gameIsRunning) {
            // If hunter is killed
            if (main.hunters.contains(event.getEntity())) {
                event.getDrops().removeIf(i -> i.getType() == Material.COMPASS);
                if (main.runners.contains(event.getEntity().getKiller().getPlayer())) { // If runner is killer
                    Player killer = Bukkit.getPlayer(event.getEntity().getKiller().getName());
                    main.hunterDeaths.put(event.getEntity().getPlayer(), main.hunterDeaths.get(event.getEntity().getPlayer()) + 1); // Increases death total for hunter
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
            if (main.runners.contains(event.getEntity())){
                if (main.hunters.contains(event.getEntity().getKiller())){ // If hunter is killer
                    main.runnerDeaths.put(event.getEntity().getPlayer(), main.runnerDeaths.get(event.getEntity().getPlayer())+1); // Increases death total for runner
                }
            }
        }
        else{
            main.logger.info("Game isn't running");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        // If cut clean is on
        if (main.commands.isCutClean()){
            Block blockBroken = event.getBlock();
            Material type = event.getBlock().getType();
            if (type.equals(Material.IRON_ORE)) {
                blockBroken.setType(Material.IRON_INGOT);
                event.setExpToDrop(4);
            }
            else if (type.equals(Material.GOLD_ORE)){
                blockBroken.setType(Material.GOLD_ORE);
                event.setExpToDrop(8);
            }
            else if (type.equals(Material.POTATO)){
                blockBroken.setType(Material.BAKED_POTATO);
                event.setExpToDrop(2);
            }
        }
        // If chest generate is on
        if (main.commands.chestGenerate) {
            int numberGenerated = this.random.nextInt(550);
            if (numberGenerated == 69) {
                Location blockBrokenLocation = event.getBlock().getLocation();
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
        var itemsList = ChestRandomizer.generateItems();
        for (ItemStack stack : itemsList){
            inv.addItem(stack);
        }
    }

    //todo: Make and inventory randomizer
    private void giveRandomDrop(Player player) {
        int generated = random.nextInt(32);
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
                    player.getPlayer().getInventory().addItem(new ItemStack(Material.COBBLESTONE,64));
                }
            }
            case 29 -> player.getPlayer().getInventory().addItem(new ItemStack(Material.ENCHANTING_TABLE));
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
                    Bukkit.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.RED + runnerName + " IS ELIMINATED.");
                    Bukkit.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.RED + runnerName + " ELIMINATE REMAINING RUNNERS TO WIN!");
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
        var inventoryToAdd = respawned.getPlayer().getInventory();
        var itemsToAdd = main.getItemGenerator().generateItemStack();
        for (ItemStack itemStack : itemsToAdd){
            inventoryToAdd.addItem(itemStack);
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
