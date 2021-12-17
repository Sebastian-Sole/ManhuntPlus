package manhunt_plus.chest_generation

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * TODO
 * This class generates an ItemStack with a ChestItem.
 */
class ItemGenerator {
    private val random = Random()
    private val pickaxeEnchantments = listOf(
            Enchantment.DIG_SPEED,
            Enchantment.LOOT_BONUS_BLOCKS,
            Enchantment.MENDING,
            Enchantment.DURABILITY
    )
    private val bookEnchants = listOf(
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
    )
    private val swordEnchants = listOf(
            Enchantment.DAMAGE_ALL,
            Enchantment.KNOCKBACK,
            Enchantment.LOOT_BONUS_MOBS
    )
    private val mainArmorEnchants = listOf(
            Enchantment.PROTECTION_ENVIRONMENTAL,
            Enchantment.PROTECTION_FIRE,
            Enchantment.PROTECTION_EXPLOSIONS,
            Enchantment.PROTECTION_PROJECTILE
    )
    private val helmetEnchants = listOf(
            Enchantment.OXYGEN,
            Enchantment.PROTECTION_ENVIRONMENTAL,
            Enchantment.PROTECTION_EXPLOSIONS,
            Enchantment.WATER_WORKER
    )
    private val bootsEnchants = listOf(
            Enchantment.PROTECTION_FALL,
            Enchantment.SOUL_SPEED,
            Enchantment.FROST_WALKER,
            Enchantment.PROTECTION_ENVIRONMENTAL
    )
    // Chest tiers
    private val chestTierOne = listOf(
            ChestItem(Material.DIAMOND_BOOTS, bootsEnchants),
            ChestItem(Material.DIAMOND_LEGGINGS, mainArmorEnchants),
            ChestItem(Material.DIAMOND_CHESTPLATE, mainArmorEnchants),
            ChestItem(Material.DIAMOND_HELMET, helmetEnchants),
            ChestItem(Material.DIAMOND_PICKAXE, pickaxeEnchantments),
            ChestItem(Material.DIAMOND, random.nextInt(3) + 1),
            ChestItem(Material.DIAMOND_SWORD, swordEnchants),
            ChestItem(Material.ENDER_PEARL, random.nextInt(3) + 3),
            ChestItem(true),
            ChestItem(Material.IRON_CHESTPLATE, mainArmorEnchants),
            ChestItem(Material.IRON_LEGGINGS, mainArmorEnchants),
            ChestItem(Material.IRON_BOOTS, bootsEnchants),
            ChestItem(Material.IRON_HELMET, helmetEnchants),
            ChestItem(Material.BOOKSHELF,6)
    )
    private val chestTierTwo = listOf(
        ChestItem(Material.DIAMOND_BOOTS, 1),
        ChestItem(Material.DIAMOND_HELMET, 1),
        ChestItem(Material.ENDER_PEARL, random.nextInt(5) + 2),
        ChestItem(Material.ENCHANTING_TABLE, 1),
        ChestItem(Material.IRON_INGOT, random.nextInt(16) + 6),
        ChestItem(Material.DIAMOND, 1),
        ChestItem(Material.PIGLIN_SPAWN_EGG, 5),
        ChestItem(Material.OBSIDIAN, 5),
        ChestItem(Material.ARROW, 15),
        ChestItem(Material.IRON_CHESTPLATE, 1),
        ChestItem(Material.IRON_LEGGINGS, 1),
        ChestItem(Material.IRON_BOOTS, bootsEnchants),
        ChestItem(Material.IRON_HELMET, helmetEnchants),
        ChestItem(Material.BOOK,12),
        ChestItem(Material.BOOKSHELF,4)
    )
    private val chestTierThree = listOf(
        ChestItem(Material.GOLDEN_APPLE, 1),
        ChestItem(Material.IRON_INGOT, random.nextInt(13) + 1),
        ChestItem(Material.ENCHANTED_BOOK, bookEnchants),
        ChestItem(Material.ANVIL, 1),
        ChestItem(Material.GOLD_BLOCK, random.nextInt(2) + 1),
        ChestItem(Material.GOLD_INGOT, random.nextInt(15) + 15),
        ChestItem(Material.ENDER_PEARL, random.nextInt(4) + 2),
        ChestItem(Material.BUCKET, 1),
        ChestItem(Material.OBSIDIAN, 3),
        ChestItem(Material.BOOK,6),
        ChestItem(Material.BOOKSHELF,2)
    )
    private val chestTierFour = listOf(
        ChestItem(Material.SHULKER_BOX, 1),
        ChestItem(Material.BEEF, random.nextInt(10) + 10),
        ChestItem(Material.COAL, random.nextInt(10) + 10),
        ChestItem(Material.GOLD_INGOT, random.nextInt(9) + 12),
        ChestItem(Material.IRON_INGOT, random.nextInt(6) + 1),
        ChestItem(Material.GOLD_NUGGET, 50),
        ChestItem(Material.GUNPOWDER, 5),
        ChestItem(Material.FEATHER, 7),
        ChestItem(Material.FLINT, 3),
        ChestItem(Material.BOOK,3),
        ChestItem(Material.ENDER_PEARL, random.nextInt(2) + 1)
        )
    private val chestTierFive = listOf(
        ChestItem(Material.OAK_PLANKS, 32),
        ChestItem(Material.TORCH, 32),
        ChestItem(Material.COAL, random.nextInt(20) + 5),
        ChestItem(Material.HAY_BLOCK, 16),
        ChestItem(Material.EXPERIENCE_BOTTLE, 10),
        ChestItem(Material.IRON_INGOT, random.nextInt(3) + 1),
        ChestItem(Material.SHIELD, 1),
        ChestItem(Material.CYAN_BED, 1),
        ChestItem(Material.STICK, 10),
        ChestItem(Material.GOLD_NUGGET, 27),
        ChestItem(Material.IRON_NUGGET, 27),
        ChestItem(Material.BOOK,1),

    )
    private val chestTiers = listOf(
            chestTierOne,
            chestTierTwo,
            chestTierThree,
            chestTierFour,
            chestTierFive
    )
    // Supply drop tiers
    private val supplyDropTierOne = listOf(
        ChestItem(Material.DIAMOND_BOOTS, bootsEnchants),
        ChestItem(Material.DIAMOND_LEGGINGS, mainArmorEnchants),
        ChestItem(Material.DIAMOND_CHESTPLATE, mainArmorEnchants),
        ChestItem(Material.DIAMOND_HELMET, helmetEnchants),
        ChestItem(Material.DIAMOND_PICKAXE, pickaxeEnchantments),
        ChestItem(Material.DIAMOND, random.nextInt(3) + 2),
        ChestItem(Material.DIAMOND_SWORD, swordEnchants),
        ChestItem(Material.ENDER_PEARL, random.nextInt(3) + 4),
        ChestItem(true),
        ChestItem(Material.BOOKSHELF,6),
        ChestItem(Material.IRON_INGOT, 15),
        ChestItem(Material.GOLD_BLOCK,4),
        ChestItem(Material.ENCHANTED_BOOK,bookEnchants),
        ChestItem(Material.ANVIL,1),
        ChestItem(Material.BLAZE_ROD, 2),
    )
    private val supplyDropTierTwo = listOf(
        ChestItem(Material.OBSIDIAN, 5),
        ChestItem(Material.ARROW, 15),
        ChestItem(Material.IRON_CHESTPLATE, mainArmorEnchants),
        ChestItem(Material.IRON_LEGGINGS, mainArmorEnchants),
        ChestItem(Material.IRON_BOOTS, bootsEnchants),
        ChestItem(Material.IRON_HELMET, helmetEnchants),
        ChestItem(Material.BOOK,12),
        ChestItem(Material.BOOKSHELF,4),
        ChestItem(Material.GOLD_BLOCK,2),
        ChestItem(Material.ENDER_PEARL, 3),
        ChestItem(Material.PIGLIN_SPAWN_EGG, 5),
        ChestItem(Material.BLAZE_ROD, 1)
    )
    private val supplyDropTierThree = listOf(
        ChestItem(Material.GOLDEN_APPLE, 2),
        ChestItem(Material.IRON_CHESTPLATE, 1),
        ChestItem(Material.IRON_LEGGINGS, 1),
        ChestItem(Material.IRON_BOOTS, 1),
        ChestItem(Material.IRON_HELMET, 1),
        ChestItem(Material.COOKED_BEEF, 32),
        ChestItem(Material.OBSIDIAN, 2),
        ChestItem(Material.IRON_INGOT, 5),
        ChestItem(Material.COOKED_BEEF, 32),
        ChestItem(Material.FLINT_AND_STEEL,1)
    )
    private val supplyDropTiers = listOf(
        supplyDropTierOne,
        supplyDropTierTwo,
        supplyDropTierThree
    )


    /**
     * Generates a random item from the tier
     *
     * @param tier which tier should be used
     * @param type the type of chest being generated, either a supply drop or a regular chest for block break events
     * @return a random chestItem from the given material
     */
    fun generateChestItemStack(tier: Int, type: String): ItemStack? {
        var itemStack: ItemStack? = null
        when (type){
            "chest" -> itemStack = chestTiers[tier][random.nextInt(chestTiers[tier].size)].createItemStack()
            "supplyDrop" -> itemStack = supplyDropTiers[tier][random.nextInt(supplyDropTiers[tier].size)].createItemStack()
        }
        return itemStack
    }


    fun size(): Int {
        return chestTiers.size
    }
}