package com.yoonicode.minecraftmanhuntplus;

public class PauseHandler {

    public static void pause(PluginMain main){
        main.getConfig().set("gameState",main.getGameState());
        main.getConfig().set("paused",true);
        main.saveConfig();
    }

    public static void unPause(PluginMain main){
        main.setGameState(main.getConfig().getInt("gameState"));
        main.getConfig().set("pause",false);
        main.saveConfig();
    }

    public static void start(PluginMain main){
        // If starting the plugin after a server stop, but also after a pause
        if (main.getConfig().getBoolean("paused")) {
            unPause(main);
        }
        // If starting the plugin and game from scratch
        else{
            main.setGameState(0);
            main.getConfig().set("gameState",0);
            main.getConfig().set("paused",false);
            main.saveConfig();
        }
    }

    public static void end(PluginMain main){
        main.getConfig().set("gameState",0);
        main.getConfig().set("paused", false);
        main.saveConfig();
    }

    public static void onEnable(PluginMain main){
        if (main.isGameIsOver()){
            main.getConfig().set("gameOver", false);
            main.saveConfig();
        }
    }

    public static void onDisable(PluginMain main){
        if (main.isGameIsOver()){
            end(main);
        }
        else{
            pause(main);
        }
    }

    // This could cause problems
    public static void updateGame(PluginMain main){
        main.getConfig().set("gameState",main.getGameState());
        main.getConfig().set("paused",main.isPaused());
        main.getConfig().set("gameOver",main.isGameIsOver());
        main.saveConfig();
    }


}
