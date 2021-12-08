package com.yoonicode.minecraftmanhuntplus.game_state;

import com.yoonicode.minecraftmanhuntplus.PluginMain;
import org.bukkit.Bukkit;

import java.util.HashMap;

public class GameStateCalculator {

    private double gameState;
    private double runnerAchievements;
    private double deaths;
    private double deathWeight = 1.1;
    private final PluginMain main;
    private HashMap<Achievement, Integer> acheivementScore = new HashMap<>();

    public GameStateCalculator(PluginMain main) { // This field should be from the config.yml
        this.main = main;
        gameState = main.getConfig().getDouble("gameState");
        for (Achievement achievement : Achievement.values()){
            acheivementScore.put(achievement,0);
        }
    }

    public void updateAchievement(Achievement achievement){
        var level = achievement.getAchivementLevel();
        if (level > (int) gameState) {
            acheivementScore.put(achievement, acheivementScore.get(achievement)+1);
            runnerAchievements = level;
            if (this.acheivementScore.get(achievement) >= (double) main.runners.size()/2){
                gameState = calculateGameState();
                main.setGameState(gameState);
                Bukkit.broadcastMessage("Game state is now: " + main.getGameState());
            }
        }
    }

    public void updateDeaths() {
        deaths++;
        gameState = calculateGameState();
        main.setGameState(gameState);
        Bukkit.broadcastMessage("Game state is now: " + main.getGameState());
    }


    public double calculateGameState(){
        return (runnerAchievements + ((deaths/(double) main.hunters.size()) * Math.pow(deathWeight, main.runners.size())))/2;
    }

}
