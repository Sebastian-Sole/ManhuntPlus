package com.yoonicode.minecraftmanhuntplus.respawn_inventory;

import com.yoonicode.minecraftmanhuntplus.PluginMain;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemGenerator  {
    PluginMain main;
    // Fields

    // Die in First attack (rush)
    private ArrayList<ItemStack> respawnOne = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.WOODEN_AXE),
            new ItemStack(Material.WOODEN_PICKAXE),
            new ItemStack(Material.WOODEN_SHOVEL),
            new ItemStack(Material.WOODEN_SWORD),
            new ItemStack(Material.COAL,5),
            new ItemStack(Material.BREAD, 5),
            new ItemStack(Material.TORCH,5)
    ));

    // Die in Second attack (limited resources or almost full iron)
    private ArrayList<ItemStack> respawnTwo = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.WOODEN_AXE),
            new ItemStack(Material.STONE_PICKAXE),
            new ItemStack(Material.STONE_SHOVEL),
            new ItemStack(Material.STONE_SWORD),
            new ItemStack(Material.COAL,10),
            new ItemStack(Material.BREAD, 10),
            new ItemStack(Material.TORCH,10),
            new ItemStack(Material.IRON_BOOTS),
            new ItemStack(Material.IRON_HELMET),
            new ItemStack(Material.IRON_NUGGET,3),
            new ItemStack(Material.SHIELD)
    ));

    // Die in Nether battle
    private ArrayList<ItemStack> respawnThree = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.STONE_AXE),
            new ItemStack(Material.IRON_PICKAXE),
            new ItemStack(Material.STONE_SHOVEL),
            new ItemStack(Material.STONE_SWORD),
            new ItemStack(Material.COAL,15),
            new ItemStack(Material.BREAD, 20),
            new ItemStack(Material.TORCH,15),
            new ItemStack(Material.IRON_CHESTPLATE),
            new ItemStack(Material.IRON_LEGGINGS),
            new ItemStack(Material.SHIELD),
            new ItemStack(Material.COOKED_BEEF, 7)
    ));

    // Die in Nether battle 2 or overworld
    private ArrayList<ItemStack> respawnFour = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.IRON_AXE),
            new ItemStack(Material.IRON_PICKAXE),
            new ItemStack(Material.STONE_SHOVEL),
            new ItemStack(Material.STONE_SWORD),
            new ItemStack(Material.COAL,15),
            new ItemStack(Material.BREAD, 20),
            new ItemStack(Material.TORCH,15),
            new ItemStack(Material.IRON_CHESTPLATE),
            new ItemStack(Material.IRON_LEGGINGS),
            new ItemStack(Material.SHIELD),
            new ItemStack(Material.COOKED_BEEF, 7),
            new ItemStack(Material.FURNACE,1)
    ));


    public ItemGenerator(PluginMain main) {
        this.main = main;
    }

    private ArrayList<ArrayList<ItemStack>> respawnTiers = new ArrayList<>(Arrays.asList(
            respawnOne,
            respawnTwo,
            respawnThree,
            respawnFour
    ));

    public ArrayList<ItemStack> generateItemStack() {
        return respawnTiers.get(main.getGameState());
    }

}
