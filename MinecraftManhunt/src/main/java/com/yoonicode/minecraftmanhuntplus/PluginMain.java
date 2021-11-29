package com.yoonicode.minecraftmanhuntplus;

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
    public ArrayList<String> hunters = new ArrayList<String>();
    public ArrayList<String> runners = new ArrayList<String>();
    public ArrayList<String> spectators = new ArrayList<String>();
    public HashMap<String, String> targets = new HashMap<String, String>();
    public HashMap<String, Location> portals = new HashMap<String, Location>();
    public Logger logger;
    public World world;
    public PluginCommands commands;
    public boolean debugMode = false;
    public HashMap<String, Integer> hunterDeaths = new HashMap<>();
    public HashMap<String, Integer> runnerDeaths = new HashMap<>();

    public boolean playerIsOnTeam(Player player){
        String name = player.getName();
        return hunters.contains(name) || runners.contains(name) || spectators.contains(name);
    }

    @Override
    public void onEnable() {
        logger = Logger.getLogger("com.yoonicode.minecraftmanhuntplus.PluginMain");
        logger = getLogger();
        logger.info("Minecraft Manhunt plugin enabled!");
        saveDefaultConfig();
        debugMode = getConfig().getBoolean("debugMode", false);
        getServer().getPluginManager().registerEvents(new PluginListener(this), this);

        commands = new PluginCommands(this);
        for(String command : PluginCommands.registeredCommands){
            this.getCommand(command).setExecutor(commands);
        }

        ScoreboardManager scoreboardManager = getScoreboardManager();
        Scoreboard board = scoreboardManager.getMainScoreboard();

        List<World> worlds = Bukkit.getWorlds();
        if(worlds.size() < 1){
            logger.warning("Could not detect main world! Plugin will not work.");
        }
        world = worlds.get(0);

    }

    public World getWorld() {
        return world;
    }


    @Override
    public void onDisable() {
        logger.info("Minecraft Manhunt plugin disabled!");
    }

}
