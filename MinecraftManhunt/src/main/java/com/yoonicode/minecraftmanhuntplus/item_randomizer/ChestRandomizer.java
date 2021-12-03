package com.yoonicode.minecraftmanhuntplus.item_randomizer;

import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.Random;

public class ChestRandomizer {

    private static Random random = new Random();
    private static ItemGenerator itemGenerator = new ItemGenerator();

    /**
     * Generates items to add in a chest
     *
     * @return an array list of item stacks with enchantments (if enchantable) to be added to the chest
     */
    public static ArrayList<ItemStack> generateItems() {
        ArrayList<ItemStack> itemsToAdd = new ArrayList<>();
        int numberOfItemsToAdd = random.nextInt(6) + 5; // At least 5, max 10
        for (int i = 0; i < numberOfItemsToAdd; i++) { //Generate that amount of random items
            int generatedNumber = random.nextInt(24) + 1;
            switch (generatedNumber) {
                case 1, 2 -> itemsToAdd.add(itemGenerator.generateItem(0));
                case 3, 4, 5, 6 -> itemsToAdd.add(itemGenerator.generateItem(1));
                case 7, 8, 9, 10, 11 -> itemsToAdd.add(itemGenerator.generateItem(2));
                case 12, 13, 14, 15, 16, 17 -> itemsToAdd.add(itemGenerator.generateItem(3));
                case 18, 19, 20, 21, 22, 23, 24 -> itemsToAdd.add(itemGenerator.generateItem(4));
            }
        }
        return itemsToAdd;
    }

}
