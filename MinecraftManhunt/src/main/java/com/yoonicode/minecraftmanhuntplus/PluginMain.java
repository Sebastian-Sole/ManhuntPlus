package com.yoonicode.minecraftmanhuntplus;

import com.yoonicode.minecraftmanhuntplus.respawn_inventory.InventoryGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getScoreboardManager;

public class PluginMain extends JavaPlugin {
    public ArrayList<Player> hunters = new ArrayList<Player>();
    public ArrayList<Player> runners = new ArrayList<Player>();
    public ArrayList<Player> spectators = new ArrayList<Player>();
    public HashMap<String, String> targets = new HashMap<String, String>();
    public HashMap<String, Location> portals = new HashMap<String, Location>();
    public Logger logger;
    public World world;
    public PluginCommands commands;
    public boolean debugMode = false;
    boolean compassEnabledInNether;
    public HashMap<Player, Integer> hunterDeaths = new HashMap<>();
    public HashMap<Player, Integer> runnerDeaths = new HashMap<>();
    private TaskManager taskManager = new TaskManager(this);
    private int gameState = 0;
    private InventoryGenerator itemGenerator;
    private boolean isPaused;
    private boolean gameIsOver;

    public boolean playerIsOnTeam(Player player){
        return hunters.stream().anyMatch(member->member.getName().equals(player.getName()))
                || runners.stream().anyMatch(member->member.getName().equals(player.getName()))
                || spectators.stream().anyMatch(member->member.getName().equals(player.getName()));
    }

    @Override
    public void onEnable() {
        logger = Logger.getLogger("com.yoonicode.minecraftmanhuntplus.PluginMain");
        logger = getLogger();
        logger.info("Minecraft Manhunt plugin enabled!");
        List<World> worlds = Bukkit.getWorlds();
        if(worlds.size() < 1){
            logger.warning("Could not detect main world! Plugin will not work.");
        }
        world = worlds.get(0);
        saveDefaultConfig();
        debugMode = getConfig().getBoolean("debugMode", false);
        compassEnabledInNether = getConfig().getBoolean("compassEnabledInNether", true);
        getServer().getPluginManager().registerEvents(new PluginListener(this), this);
        commands = new PluginCommands(this);
        for(String command : PluginCommands.registeredCommands){
            this.getCommand(command).setExecutor(commands);
        }
        this.itemGenerator = new InventoryGenerator(this);
        ScoreboardManager scoreboardManager = getScoreboardManager();
        Scoreboard board = scoreboardManager.getMainScoreboard();
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "weather clear 199999999");
        PauseHandler.onEnable(this);
    }

    public World getWorld() {
        return world;
    }


    @Override
    public void onDisable() {
        logger.info("Minecraft Manhunt plugin disabled!");
        PauseHandler.onDisable(this);
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public int getGameState() {
        return gameState;
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
    }

    public void incrementGameState(){
        this.gameState++;
        PauseHandler.updateGame(this);
    }

    public InventoryGenerator getItemGenerator() {
        return itemGenerator;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public boolean isGameIsOver() {
        return gameIsOver;
    }

    public void setGameIsOver(boolean gameIsOver) {
        this.gameIsOver = gameIsOver;
        getConfig().set("gameOver",true);
        saveConfig();
    }
}
