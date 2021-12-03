package com.yoonicode.minecraftmanhuntplus.item_randomizer;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.Random;

public class ChestItem {

    private Material material;
    private int numberGenerated = 1;
    private ArrayList<Enchantment> enchantments = new ArrayList<>();
    private Random random = new Random();
    private int enchantmentStrengths;
    private int enchantsGenerated = random.nextInt(2)+1;

    public ChestItem(Material material, int numberGenerated) {
        this.material = material;
        this.numberGenerated = numberGenerated;
    }

    public ChestItem(Material material, ArrayList<Enchantment> enchantments){
        this.material = material;
        this.enchantments.addAll(enchantments);
    }


    /**
     * Provides the item stack with the generated enchantments
     *
     * @return item stack with enchantments
     */
    public ItemStack getItemStack() {
        var itemStack = new ItemStack(this.material, numberGenerated);
        if (enchantsGenerated == 0){
            return itemStack;
        }
        // If item to be generated is unenchantable, then just return the item
        if (this.enchantments.size() == 0) {
            return itemStack;
        }
        // Set the strength of the enchantment
        var enchant = enchantments.get(random.nextInt(enchantments.size()));
        this.enchantmentStrengths = random.nextInt(enchant.getMaxLevel())+1;
        if (enchantmentStrengths > 2){
            this.enchantmentStrengths = 2;
        }

        // Apply enchant
        for (int i = 0; i <= enchantsGenerated; i++) {
            if (this.material.equals(Material.ENCHANTED_BOOK)) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
                meta.addStoredEnchant(enchant, enchantmentStrengths, true);
                itemStack.setItemMeta(meta);
            } else {
                itemStack.addEnchantment(enchantments.get(random.nextInt(enchantments.size())), enchantmentStrengths);

            }
        }
        return itemStack;
    }
}

