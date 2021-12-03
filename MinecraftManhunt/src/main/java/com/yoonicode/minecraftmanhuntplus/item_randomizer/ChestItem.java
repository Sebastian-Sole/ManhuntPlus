package com.yoonicode.minecraftmanhuntplus.item_randomizer;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ChestItem {

    private Material material;
    private int numberGenerated = 1;
    private ArrayList<Enchantment> enchantments = new ArrayList<>();
    private Random random = new Random();
    private int enchantmentStrengths;
    private int enchantsGenerated = random.nextInt(2)+1;
    private boolean isPotion;
    private ArrayList<PotionType> potionTypes = new ArrayList<>(Arrays.asList(
            PotionType.SPEED,
            PotionType.FIRE_RESISTANCE,
            PotionType.INSTANT_HEAL,
            PotionType.INVISIBILITY,
            PotionType.NIGHT_VISION,
            PotionType.REGEN,
            PotionType.WATER_BREATHING,
            PotionType.JUMP
    ));

    public ChestItem(Material material, int numberGenerated) {
        this.material = material;
        this.numberGenerated = numberGenerated;
    }

    public ChestItem(Material material, ArrayList<Enchantment> enchantments){
        this.material = material;
        this.enchantments.addAll(enchantments);
    }

    public ChestItem(boolean isPotion){
        this.isPotion = isPotion;
    }


    /**
     * Provides the item stack with the generated enchantments or a potion
     *
     * @return item stack with enchantments or potion
     */
    public ItemStack getItemStack() {
        if (isPotion){
            ItemStack potion = new ItemStack(Material.POTION, 1);
            PotionMeta meta = (PotionMeta) potion.getItemMeta();
            meta.setBasePotionData(new PotionData(potionTypes.get(random.nextInt(potionTypes.size()))));
            potion.setItemMeta(meta);
            return potion;
        }
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

