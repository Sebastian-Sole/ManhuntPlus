package com.yoonicode.minecraftmanhuntplus.respawn_inventory;

import com.yoonicode.minecraftmanhuntplus.PluginMain;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class InventoryGenerator {
    PluginMain main;
    private int level;
    private double modifier;
    private Random random = new Random();
    // Fields

    // 0-1
    private ArrayList<ItemStack> respawnOne = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.WOODEN_AXE),
            new ItemStack(Material.WOODEN_PICKAXE),
            new ItemStack(Material.WOODEN_SHOVEL),
            new ItemStack(Material.WOODEN_SWORD),
            new ItemStack(Material.COAL,5),
            new ItemStack(Material.BREAD, 5),
            new ItemStack(Material.TORCH,5)
    ));

    // 1-2
    private ArrayList<ItemStack> respawnTwo = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.STONE_AXE),
            new ItemStack(Material.STONE_PICKAXE),
            new ItemStack(Material.STONE_SHOVEL),
            new ItemStack(Material.STONE_SWORD),
            new ItemStack(Material.IRON_HELMET)
    ));

    // 2-3
    private ArrayList<ItemStack> respawnThree = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.STONE_AXE),
            new ItemStack(Material.IRON_PICKAXE),
            new ItemStack(Material.IRON_SHOVEL),
            new ItemStack(Material.STONE_SWORD),
            new ItemStack(Material.GOLDEN_CHESTPLATE),
            new ItemStack(Material.GOLDEN_LEGGINGS),
            new ItemStack(Material.BREAD,10),
            new ItemStack(Material.TORCH,10)
    ));

    // 3-4
    private ArrayList<ItemStack> respawnFour = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.IRON_AXE),
            new ItemStack(Material.IRON_PICKAXE),
            new ItemStack(Material.STONE_SHOVEL),
            new ItemStack(Material.STONE_SWORD),
            new ItemStack(Material.IRON_HELMET),
            new ItemStack(Material.IRON_BOOTS),
            new ItemStack(Material.FURNACE),
            new ItemStack(Material.COAL, 3),
            new ItemStack(Material.OAK_LOG,10)
    ));

    // 4-5
    private ArrayList<ItemStack> respawnFive = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.IRON_AXE),
            new ItemStack(Material.IRON_PICKAXE),
            new ItemStack(Material.STONE_SHOVEL),
            new ItemStack(Material.STONE_SWORD),
            new ItemStack(Material.IRON_HELMET),
            new ItemStack(Material.IRON_BOOTS),
            new ItemStack(Material.GOLDEN_CHESTPLATE),
            new ItemStack(Material.GOLDEN_LEGGINGS),
            new ItemStack(Material.FURNACE),
            new ItemStack(Material.COAL,3),
            new ItemStack(Material.OAK_LOG,2),
            new ItemStack(Material.COOKED_BEEF,5)
    ));

    // 5-6
    private ArrayList<ItemStack> respawnSix = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.IRON_AXE),
            new ItemStack(Material.IRON_PICKAXE),
            new ItemStack(Material.STONE_SHOVEL),
            new ItemStack(Material.STONE_SWORD),
            new ItemStack(Material.IRON_HELMET),
            new ItemStack(Material.IRON_BOOTS),
            new ItemStack(Material.GOLDEN_CHESTPLATE),
            new ItemStack(Material.IRON_LEGGINGS),
            new ItemStack(Material.FURNACE),
            new ItemStack(Material.COAL,3),
            new ItemStack(Material.OAK_LOG,2),
            new ItemStack(Material.COOKED_BEEF,10)
    ));

    // 6-7
    private ArrayList<ItemStack> respawnSeven = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.IRON_AXE),
            new ItemStack(Material.IRON_PICKAXE),
            new ItemStack(Material.STONE_SHOVEL),
            new ItemStack(Material.STONE_SWORD),
            new ItemStack(Material.IRON_HELMET),
            new ItemStack(Material.IRON_BOOTS),
            new ItemStack(Material.IRON_CHESTPLATE),
            new ItemStack(Material.IRON_LEGGINGS),
            new ItemStack(Material.FURNACE),
            new ItemStack(Material.COAL,3),
            new ItemStack(Material.OAK_LOG,2),
            new ItemStack(Material.COOKED_BEEF,5)
    ));

    // 7-8
    private ArrayList<ItemStack> respawnEight = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.IRON_AXE),
            new ItemStack(Material.IRON_PICKAXE),
            new ItemStack(Material.STONE_SHOVEL),
            new ItemStack(Material.STONE_SWORD),
            new ItemStack(Material.IRON_HELMET),
            new ItemStack(Material.IRON_BOOTS),
            new ItemStack(Material.IRON_CHESTPLATE),
            new ItemStack(Material.IRON_LEGGINGS),
            new ItemStack(Material.FURNACE),
            new ItemStack(Material.COAL,3),
            new ItemStack(Material.OAK_LOG,2),
            new ItemStack(Material.COOKED_BEEF,10),
            new ItemStack(Material.SHIELD)
    ));

    // 8-9
    private ArrayList<ItemStack> respawnNine = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.IRON_AXE),
            new ItemStack(Material.IRON_PICKAXE),
            new ItemStack(Material.STONE_SHOVEL),
            new ItemStack(Material.STONE_SWORD),
            new ItemStack(Material.IRON_HELMET),
            new ItemStack(Material.IRON_BOOTS),
            new ItemStack(Material.IRON_CHESTPLATE),
            new ItemStack(Material.IRON_LEGGINGS),
            new ItemStack(Material.FURNACE),
            new ItemStack(Material.COAL,3),
            new ItemStack(Material.OAK_LOG,2),
            new ItemStack(Material.COOKED_BEEF,15),
            new ItemStack(Material.SHIELD),
            new ItemStack(Material.BOW),
            new ItemStack(Material.ARROW,10)
    ));

    private ArrayList<ItemStack> tierOne = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.IRON_ORE, random.nextInt(5)+1),
            new ItemStack(Material.BREAD, random.nextInt(5)+5)
    ));

    private ArrayList<ItemStack> tierTwo = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.IRON_HELMET),
            new ItemStack(Material.IRON_BOOTS),
            new ItemStack(Material.IRON_ORE, random.nextInt(5)+1),
            new ItemStack(Material.BREAD, random.nextInt(5)+5),
            new ItemStack(Material.COAL, random.nextInt(5)+5),
            new ItemStack(Material.OAK_LOG, random.nextInt(3)+5)
            ));

    private ArrayList<ItemStack> tierThree = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.BREAD, random.nextInt(5)+1),
            new ItemStack(Material.COAL, random.nextInt(10)+1),
            new ItemStack(Material.OAK_LOG, random.nextInt(8)+5),
            new ItemStack(Material.IRON_ORE, random.nextInt(5)+1),
            new ItemStack(Material.TORCH, 32)
            ));

    private ArrayList<ItemStack> tierFour = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.BREAD, random.nextInt(5)+1),
            new ItemStack(Material.COAL, random.nextInt(10)+1),
            new ItemStack(Material.OAK_LOG, random.nextInt(5)+1),
            new ItemStack(Material.IRON_ORE, random.nextInt(5)+1),
            new ItemStack(Material.ENDER_PEARL)
    ));

    private ArrayList<ItemStack> tierFive = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.ENDER_PEARL),
            new ItemStack(Material.DIAMOND, random.nextInt(2))
    ));


    private ArrayList<ArrayList<ItemStack>> respawnLevel = new ArrayList<>(Arrays.asList(
            respawnOne,
            respawnTwo,
            respawnThree,
            respawnFour,
            respawnFive,
            respawnSix,
            respawnSeven,
            respawnEight,
            respawnNine
    ));

    private ArrayList<ArrayList<ItemStack>> effectTier = new ArrayList<>(Arrays.asList(
            tierOne,
            tierTwo,
            tierThree,
            tierFour,
            tierFive
    ));


    public InventoryGenerator(PluginMain main) {
        this.main = main;
    }


    public ArrayList<ItemStack> generateItemStack() {
        calculateScore(main.getGameState());
        ArrayList<ItemStack> mainItems = new ArrayList<>();
        mainItems.addAll(respawnLevel.get(level));
        boolean addItems = Math.random() < this.modifier;
        ArrayList<ItemStack> extraItems = new ArrayList<>();
        if (addItems){
            ArrayList<ItemStack> activeTier;
            if (level == 0){
                activeTier = effectTier.get(0);
            }
            else {
                activeTier = effectTier.get(level-1);
            }
            for (ItemStack itemStack : activeTier) {
                if (Math.random() < modifier) {
                    extraItems.add(itemStack);
                }
            }
        }
        mainItems.addAll(extraItems);
        return mainItems;
    }

    private void calculateScore(double score) {
        this.level = (int) score;
        String scoreAsString = Double.toString(score);
        String decimalString = scoreAsString.substring(scoreAsString.indexOf("."));
        this.modifier = getDecimal(decimalString);
    }

    private double getDecimal(String s){
        String fullDecimal = "0" + s;
        return Double.parseDouble(fullDecimal);
    }

}
