package com.yoonicode.minecraftmanhuntplus.chest_generation

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.potion.PotionType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionData
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import java.util.*


class ChestItem {
    private var material: Material? = null
    private var numberGenerated = 1
    private val enchantments = mutableListOf<Enchantment>()
    private val random = Random()
    private var enchantmentStrengths = 0
    private val enchantsGenerated = random.nextInt(2)
    private var isPotion = false
    private val potionTypes = listOf(
            PotionType.SPEED,
            PotionType.FIRE_RESISTANCE,
            PotionType.INSTANT_HEAL,
            PotionType.INVISIBILITY,
            PotionType.NIGHT_VISION,
            PotionType.REGEN,
            PotionType.WATER_BREATHING,
            PotionType.JUMP
    )

    constructor(material: Material?, numberGenerated: Int) {
        this.material = material
        this.numberGenerated = numberGenerated
    }

    constructor(material: Material?, enchantments: List<Enchantment>?) {
        this.material = material
        this.enchantments.addAll(enchantments!!)
    }

    constructor(isPotion: Boolean) {
        this.isPotion = isPotion
    }

    /**
     * Provides the item stack with the generated enchantments or a potion
     *
     * @return item stack with enchantments or potion
     */
     fun createItemStack(): ItemStack? {
        if (isPotion) {
            val potion = ItemStack(Material.POTION, 1)
            val meta = potion.itemMeta as PotionMeta?
            meta!!.basePotionData = PotionData(potionTypes[random.nextInt(potionTypes.size)])
            potion.itemMeta = meta
            return potion
        }
        val itemStack = material?.let { ItemStack(it, numberGenerated) }
        if (enchantsGenerated == 0) {
            return itemStack
        }
        // If item to be generated is unenchantable, then just return the item
        if (enchantments.size == 0) {
            return itemStack
        }
        // Set the strength of the enchantment
        val enchant = enchantments[random.nextInt(enchantments.size)]
        enchantmentStrengths = 1

        // Apply enchant
        for (i in 0..enchantsGenerated) {
            if (material == Material.ENCHANTED_BOOK) {
                val meta = itemStack?.itemMeta as EnchantmentStorageMeta?
                meta!!.addStoredEnchant(enchant, enchantmentStrengths, true)
                itemStack?.itemMeta = meta
            } else {
                itemStack?.addEnchantment(enchantments[random.nextInt(enchantments.size)], enchantmentStrengths)
            }
        }
        return itemStack
    }
}