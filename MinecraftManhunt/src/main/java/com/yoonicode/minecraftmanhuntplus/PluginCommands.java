package com.yoonicode.minecraftmanhuntplus;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PluginCommands implements CommandExecutor {

    public static final String[] registeredCommands = {
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
            "allhelp"
    };
    public boolean hitHasRegistered; // used for startGameByHit option
    public boolean extraDrops;
    public boolean chestGenerate;

    int compassTask = -1;
    int dangerLevelTask = -1;
    public boolean gameIsRunning = false;

    boolean worldBorderModified;
    private final PluginMain main;
    public boolean runnerHelp = false;
    public boolean hunterHelp = false;
    public boolean hasteBoost = false;
    private boolean cutClean = false;

    public PluginCommands(PluginMain main) {
        this.main = main;
    }



    public List<String> getCompletions(String[] args, List<String> existingCompletions){
        switch (args[0]){
            case "/hunter":
            case "/runner":
            case "/spectator": {
                return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
            }
            case "/start":
            case "/end":
            case "/chestgenerate":
            case "/compass":
            case "/clearteams":
            case "/hasteboost":
            case "/allhelp":
                return new ArrayList<String>();
            case "/setheadstart": {
                return new ArrayList<String>(){
                    {
                        add("0");
                        add("30");
                        add("60");
                    }
                };
            }
            default:
                return existingCompletions;

        }
    }

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if ("hunter".equals(label)) {
            if (args.length != 1) return false;
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                commandSender.sendMessage("Target is not online");
                return false;
            }
            if (isOnTargetTeam(commandSender, target, main.hunters)) return true;
            main.runners.remove(target);
            main.spectators.remove(target);
            main.hunters.add(target);
            main.hunterDeaths.put(target,0);
            Bukkit.broadcastMessage(target.getName() + " is now a " + ChatColor.RED + ChatColor.BOLD + "HUNTER!");
            return true;
        }
        else if ("runner".equals(label)) {
            if (args.length != 1) return false;
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                commandSender.sendMessage("Target is not online");
                return false;
            }
            if (isOnTargetTeam(commandSender, target, main.runners)) return true;
            main.hunters.remove(target);
            main.spectators.remove(target);
            main.runners.add(target);
            main.runnerDeaths.put(target,0);

            Bukkit.broadcastMessage(target.getName() + " is now a " + ChatColor.GREEN + ChatColor.BOLD + "RUNNER!");
            return true;
        }
        else if ("spectator".equals(label)) {
            if (args.length != 1) return false;
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                commandSender.sendMessage("Target is not online");
                return false;
            }
            if (isOnTargetTeam(commandSender, target, main.spectators)) return true;
            main.runners.remove(target);
            main.hunters.remove(target);
            main.spectators.add(target);
            target.sendMessage("You have been marked as a spectator.");
            commandSender.sendMessage("Marked player as spectator");
            return true;
        }
        else if ("start".equals(label)) {
            if(gameIsRunning){
                commandSender.sendMessage("Game is already in progress. Use /end before starting another game.");
                return true;
            }
            if (main.runners.size() < 1 && !main.debugMode) {
                commandSender.sendMessage("Not enough speedrunners to start");
                return true;
            }
            commandSender.sendMessage("Starting game...");
            main.targets.clear();
            int headStartDuration = main.getConfig().getInt("headStartDuration");
            if (main.getConfig().getBoolean("clearItemDropsOnStart", false)) {
                commandSender.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:kill @e[type=item]");
            }
            updateWorld();
            updateSpectators();
            runnersState();
            huntersState(headStartDuration);
            compassTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
                public void run() {
                    main.getTaskManager().updateCompass();
                }
            }, 0L, 20L);

            if (hasteBoost) {
                Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
                    public void run() {
                        main.getTaskManager().giveHaste();
                    }
                }, 460L, 1200L);
            }

            //todo: How often does this actually need to repeat?
            Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
                @Override
                public void run() {
                    main.getTaskManager().showGlow();
                }
            }, 0,600); // 2700? More?
            gameIsRunning = true;
            sendStartMessage();
            return true;
        }
        else if ("end".equals(label)) {
            if(!gameIsRunning){
                commandSender.sendMessage("There is no game in progress. Use /start to start a new game.");
                return true;
            }
            if(main.getConfig().getBoolean("startGameByHit", false)){
                hitHasRegistered = false;
            }
            BukkitScheduler scheduler = Bukkit.getScheduler();
            if (compassTask != -1) {
                scheduler.cancelTask(compassTask);
                compassTask = -1;
            }
            if (dangerLevelTask != -1) {
                scheduler.cancelTask(dangerLevelTask);
                dangerLevelTask = -1;
            }
            worldBorderModified = false;
            Bukkit.broadcastMessage("Manhunt stopped!");
            gameIsRunning = false;
            return true;
        }
        else if("compass".equals(label)) {
            Player sender = (Player) commandSender;
            sender.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
            commandSender.sendMessage("Here you go!");
            return true;
        }
        else if("clearteams".equals(label)) {
            commandSender.sendMessage(clearTeams());
            return true;
        }
        else if("setheadstart".equals(label)) {
            main.logger.info("setheadstart called.");
            if(args.length == 0){
                commandSender.sendMessage("Provide a headstart duration as a nonnegative integer");
                return false;
            }
            int duration;
            try {
                duration = Integer.parseInt(args[0]);
            } catch(NumberFormatException e){
                commandSender.sendMessage("Headstart duration must be a nonnegative integer");
                return false;
            }
            if(duration < 0){
                commandSender.sendMessage("Headstart duration must be greater than or equal to 0");
                return false;
            }
            main.getConfig().set("headStartDuration", duration);
            main.saveConfig();
            commandSender.sendMessage("Headstart set to " + duration);
            return true;
        }
        else if("runnerhelp".equals(label)){
            if (gameIsRunning){
                commandSender.sendMessage("Game is already in progress. Restart a game to change this option");
                return true;
            }
            if (args.length != 0){
                commandSender.sendMessage("Illegal format. Use /chestgenerate.");
                return true;
            }
            runnerHelp=!runnerHelp;
            commandSender.sendMessage("Runner help is set to: " + runnerHelp);
        }
        else if("hunterhelp".equals(label)){
            if (gameIsRunning){
                commandSender.sendMessage("Game is already in progress. Restart a game to change this option");
                return true;
            }
            if (args.length != 0){
                commandSender.sendMessage("Illegal format. Use /chestgenerate.");
                return true;
            }
            hunterHelp=!hunterHelp;
            commandSender.sendMessage("Hunter help is set to: " + hunterHelp);


        }
        else if("extradrops".equals(label)){
            if (gameIsRunning){
                commandSender.sendMessage("Game is already in progress. Restart a game to change this option");
                return true;
            }
            if (args.length != 0){
                commandSender.sendMessage("Illegal format. Use /chestgenerate.");
                return true;
            }
            extraDrops=!extraDrops;
            commandSender.sendMessage("Extra drops is set to: " + extraDrops);
        }
        else if ("chestgenerate".equals(label)){
            if (gameIsRunning){
                commandSender.sendMessage("Game is already in progress. Restart a game to change this option");
                return true;
            }
            if (args.length != 0){
                commandSender.sendMessage("Illegal format. Use /chestgenerate.");
                return true;
            }
            chestGenerate = !chestGenerate;
            commandSender.sendMessage("Random Chest spawns is set to: " + chestGenerate);
            return true;
        }
        else if ("hasteboost".equals(label)){
            if (gameIsRunning){
                commandSender.sendMessage("Game is already in progress. Restart a game to change this option");
                return true;
            }
            if (args.length != 0){
                commandSender.sendMessage("Illegal format. Use /chestgenerate.");
                return true;
            }
            hasteBoost = !hasteBoost;
            commandSender.sendMessage("Haste boost is set to: " + hasteBoost);
            return true;
        }
        else if ("allhelp".equals(label)){
            if (gameIsRunning){
                commandSender.sendMessage("Game is already in progress. Restart a game to change this option");
                return true;
            }
            if (args.length != 0){
                commandSender.sendMessage("Illegal format. Use /chestgenerate.");
                return true;
            }
            runnerHelp = true;
            hunterHelp = true;
            extraDrops = true;
            chestGenerate = true;
            hasteBoost = true;
            cutClean = true;
            commandSender.sendMessage("All helper methods are enabled");
            return true;
        }
        else if ("cutclean".equals(label)){
            if (gameIsRunning){
                commandSender.sendMessage("Game is already in progress. Restart a game to change this option");
            }
            if (args.length != 0){
                commandSender.sendMessage("Illegal format. Use /chestgenerate.");
                return true;
            }
            cutClean = !cutClean;
            commandSender.sendMessage("Cut Clean is set to: " + cutClean);
            return true;
        }
        return false;

    }

    private boolean isOnTargetTeam(CommandSender commandSender, Player target, ArrayList<Player> team) {
        if (team.stream().anyMatch(player -> player.getName().equals(target.getName()))){
            commandSender.sendMessage("Target is already on this team");
            return true;
        }
        return false;
    }

    private void sendStartMessage() {
        Bukkit.broadcastMessage(
                ChatColor.DARK_RED.toString() + ChatColor.UNDERLINE + "Berner er gay!" + ChatColor.RESET + "\n" +
                        ChatColor.AQUA + "Tarun " + ChatColor.GOLD + "Tarun" + ChatColor.AQUA + " Tarun" + "\n" +
                        ChatColor.GREEN + "TarunTårnet! " + ChatColor.GOLD + "TARUNSAN! TARUNSAN! TARUNSAN!" + ChatColor.GREEN + " BI is love, BI is life -Tarun, probably" + "\n" +
                        ChatColor.UNDERLINE + ChatColor.BOLD + ChatColor.LIGHT_PURPLE + "MANHUNT STARTED!" + ChatColor.RESET + ChatColor.DARK_GRAY + " NTarunNU"
        );
    }

    private void huntersState(int headStartDuration) {
        for (Player player : main.hunters) {
            if (player == null) continue;
            player.setGameMode(GameMode.SURVIVAL);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * headStartDuration, 5));
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * headStartDuration, 3));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * headStartDuration, 10));
            player.setHealth(20.0);
            player.setFoodLevel(20);

            if (main.getConfig().getBoolean("clearHunterInvOnStart", false)) {
                player.getInventory().clear();
                player.setExp(0);
                player.setLevel(0);
            }

            player.getInventory().addItem(new ItemStack(Material.COMPASS, 1));

        }
    }

    private void runnersState() {
        for (Player player : main.runners) {
            if (player == null) continue;
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealthScale(20.0);
            player.setMaxHealth(20.0);
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 450,1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE,1));

            if (main.getConfig().getBoolean("clearRunnerInvOnStart", false)) {
                player.getInventory().clear();
                player.setExp(0);
                player.setLevel(0);
            }

        }
    }

    private void updateSpectators() {
        for (Player player : main.spectators) {
            if (player == null) continue;
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    private void updateWorld() {
        if (worldBorderModified) {
            WorldBorder wb = main.world.getWorldBorder();
            wb.setCenter(0.5, 0.5);
            wb.setSize(60000000);
        }

        List<World> worlds = Bukkit.getWorlds();
        World world1 = worlds.get(0);
        if (main.getConfig().getBoolean("setTimeToZero", true)) {
            main.world = world1;
            main.getWorld().setTime(0);
        }
    }

    public String clearTeams(){
        int playersCleared = main.hunters.size() + main.spectators.size() + main.runners.size();
        main.hunters.clear();
        main.spectators.clear();
        main.runners.clear();
        return playersCleared + " players cleared from teams";
    }

    public boolean isCutClean() {
        return cutClean;
    }
}
