package com.yoonicode.minecraftmanhuntplus.chest_generation;

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
            int generatedNumber = random.nextInt(28) + 1;
            switch (generatedNumber) {
                case 1, 2 -> itemsToAdd.add(itemGenerator.generateItemStack(0));
                case 3, 4, 5, 6, 7 -> itemsToAdd.add(itemGenerator.generateItemStack(1));
                case 8, 9, 10, 11, 12, 13 -> itemsToAdd.add(itemGenerator.generateItemStack(2));
                case 14, 15, 16, 17, 18, 19, 20 -> itemsToAdd.add(itemGenerator.generateItemStack(3));
                case 21, 22, 23, 24, 25, 26, 27, 28 -> itemsToAdd.add(itemGenerator.generateItemStack(4));
            }
        }
        return itemsToAdd;
    }

}
