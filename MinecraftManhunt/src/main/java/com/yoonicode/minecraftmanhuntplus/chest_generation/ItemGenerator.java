package com.yoonicode.minecraftmanhuntplus.chest_generation;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ItemGenerator {

    private Random random = new Random();


    private final ArrayList<Enchantment> pickaxeEnchantments = new ArrayList<>(Arrays.asList(
            Enchantment.DIG_SPEED,
            Enchantment.LOOT_BONUS_BLOCKS,
            Enchantment.MENDING,
            Enchantment.DURABILITY
    ));

    private final ArrayList<Enchantment> bookEnchants = new ArrayList<>(Arrays.asList(
            Enchantment.DAMAGE_ALL,
            Enchantment.DEPTH_STRIDER,
            Enchantment.DURABILITY,
            Enchantment.PROTECTION_FALL,
            Enchantment.ARROW_DAMAGE,
            Enchantment.ARROW_KNOCKBACK,
            Enchantment.DIG_SPEED,
            Enchantment.WATER_WORKER,
            Enchantment.LOOT_BONUS_BLOCKS,
            Enchantment.FROST_WALKER
    ));

    private final ArrayList<Enchantment> swordEncants = new ArrayList<>(Arrays.asList(
            Enchantment.DAMAGE_ALL,
            Enchantment.KNOCKBACK,
            Enchantment.LOOT_BONUS_MOBS
    ));

    private final ArrayList<Enchantment> mainArmorEnchants = new ArrayList<>(Arrays.asList(
            Enchantment.PROTECTION_ENVIRONMENTAL,
            Enchantment.PROTECTION_FIRE,
            Enchantment.PROTECTION_EXPLOSIONS,
            Enchantment.PROTECTION_PROJECTILE
    ));

    private final ArrayList<Enchantment> helmetEnchants = new ArrayList<>(Arrays.asList(
            Enchantment.OXYGEN,
            Enchantment.PROTECTION_ENVIRONMENTAL,
            Enchantment.PROTECTION_EXPLOSIONS,
            Enchantment.WATER_WORKER
    ));

    private final ArrayList<Enchantment> bootsEnchants = new ArrayList<>(Arrays.asList(
            Enchantment.PROTECTION_FALL,
            Enchantment.SOUL_SPEED,
            Enchantment.FROST_WALKER,
            Enchantment.PROTECTION_ENVIRONMENTAL
    ));

    private final ArrayList<ChestItem> tierOne = new ArrayList<>(Arrays.asList(
            new ChestItem(Material.DIAMOND_BOOTS,bootsEnchants),
            new ChestItem(Material.DIAMOND_LEGGINGS,mainArmorEnchants),
            new ChestItem(Material.DIAMOND_CHESTPLATE,mainArmorEnchants),
            new ChestItem(Material.DIAMOND_HELMET,helmetEnchants),
            new ChestItem(Material.DIAMOND_PICKAXE,pickaxeEnchantments),
            new ChestItem(Material.DIAMOND,random.nextInt(3)+1),
            new ChestItem(Material.DIAMOND_SWORD,swordEncants),
            new ChestItem(Material.ENDER_PEARL, random.nextInt(3)+3),
            new ChestItem(true),
            new ChestItem(Material.IRON_CHESTPLATE, mainArmorEnchants),
            new ChestItem(Material.IRON_LEGGINGS,mainArmorEnchants),
            new ChestItem(Material.IRON_BOOTS,bootsEnchants),
            new ChestItem(Material.IRON_HELMET,helmetEnchants)
            ));
    private final ArrayList<ChestItem> tierTwo = new ArrayList<>(Arrays.asList(
            new ChestItem(Material.DIAMOND_BOOTS,1),
            new ChestItem(Material.DIAMOND_HELMET,1),
            new ChestItem(Material.ENDER_PEARL, random.nextInt(5)+1),
            new ChestItem(Material.ENCHANTING_TABLE,1),
            new ChestItem(Material.IRON_INGOT,random.nextInt(16)+6),
            new ChestItem(Material.DIAMOND, 1),
            new ChestItem(Material.PIGLIN_SPAWN_EGG,5),
            new ChestItem(Material.OBSIDIAN,5),
            new ChestItem(Material.ARROW,15),
            new ChestItem(Material.IRON_CHESTPLATE,1 ),
            new ChestItem(Material.IRON_LEGGINGS, 1),
            new ChestItem(Material.IRON_BOOTS, bootsEnchants),
            new ChestItem(Material.IRON_HELMET, helmetEnchants)

            ));
    private final ArrayList<ChestItem> tierThree = new ArrayList<>(Arrays.asList(
            new ChestItem(Material.GOLDEN_APPLE,1),
            new ChestItem(Material.IRON_INGOT,random.nextInt(13)+1),
            new ChestItem(Material.ENCHANTED_BOOK,bookEnchants),
            new ChestItem(Material.ANVIL,1),
            new ChestItem(Material.GOLD_BLOCK,random.nextInt(2)+1),
            new ChestItem(Material.GOLD_INGOT,random.nextInt(15)+15),
            new ChestItem(Material.ENDER_PEARL,1),
            new ChestItem(Material.BUCKET,1),
            new ChestItem(Material.OBSIDIAN,3)

            ));
    private final ArrayList<ChestItem> tierFour = new ArrayList<>(Arrays.asList(
            new ChestItem(Material.SHULKER_BOX,1),
            new ChestItem(Material.BEEF,random.nextInt(10)+10),
            new ChestItem(Material.COAL,random.nextInt(10)+10),
            new ChestItem(Material.GOLD_INGOT,random.nextInt(9)+12),
            new ChestItem(Material.IRON_INGOT,random.nextInt(6)+1),
            new ChestItem(Material.GOLD_NUGGET,50),
            new ChestItem(Material.GUNPOWDER,5),
            new ChestItem(Material.FEATHER,7),
            new ChestItem(Material.FLINT,3)
            ));

    private final ArrayList<ChestItem> tierFive = new ArrayList<>(Arrays.asList(
            new ChestItem(Material.OAK_PLANKS,32),
            new ChestItem(Material.TORCH,32),
            new ChestItem(Material.COAL,random.nextInt(20)+5),
            new ChestItem(Material.HAY_BLOCK,16),
            new ChestItem(Material.EXPERIENCE_BOTTLE,10),
            new ChestItem(Material.IRON_INGOT,random.nextInt(3)+1),
            new ChestItem(Material.SHIELD,1),
            new ChestItem(Material.CYAN_BED,1),
            new ChestItem(Material.STICK,10),
            new ChestItem(Material.GOLD_NUGGET,27),
            new ChestItem(Material.IRON_NUGGET,27)
    ));

    private final ArrayList<ArrayList<ChestItem>> tiers = new ArrayList<>(Arrays.asList(
            tierOne,
            tierTwo,
            tierThree,
            tierFour,
            tierFive
    ));

    public ItemGenerator() {

    }

    /**
     * Generates a random item from the tier
     *
     * @param tier which tier should be used
     * @return a random chestItem from the given material
     */
    public ItemStack generateItemStack(int tier){
        var itemTier = tiers.get(tier);
        // Create a chestItem object from the material
        ChestItem item = itemTier.get(random.nextInt(itemTier.size()));

        // Get the enchants and data from the chestItem
        return item.getItemStack();
    }

    public int size(){
        return tiers.size();
    }



}
