package com.yoonicode.minecraftmanhuntplus.respawn_inventory

import com.yoonicode.minecraftmanhuntplus.PluginMain
import org.bukkit.inventory.ItemStack
import org.bukkit.Material
import java.util.*

/**
 * TODO
 * Generates the inventory that a respawned player gets.
 *
 * @property main the plugin main being used
 */
class InventoryGenerator(var main: PluginMain) {
    private var level = 0
    private var modifier = 0.0
    private val random = Random()

    // Fields
    // 0-1
    private val respawnOne = listOf(
            ItemStack(Material.WOODEN_AXE),
            ItemStack(Material.WOODEN_PICKAXE),
            ItemStack(Material.WOODEN_SHOVEL),
            ItemStack(Material.WOODEN_SWORD),
            ItemStack(Material.COAL, 5),
            ItemStack(Material.BREAD, 5),
            ItemStack(Material.TORCH, 5)
    )

    // 1-2
    private val respawnTwo = listOf(
            ItemStack(Material.STONE_AXE),
            ItemStack(Material.STONE_PICKAXE),
            ItemStack(Material.STONE_SHOVEL),
            ItemStack(Material.STONE_SWORD),
            ItemStack(Material.IRON_HELMET)
    )

    // 2-3
    private val respawnThree = listOf(
            ItemStack(Material.STONE_AXE),
            ItemStack(Material.IRON_PICKAXE),
            ItemStack(Material.IRON_SHOVEL),
            ItemStack(Material.STONE_SWORD),
            ItemStack(Material.GOLDEN_CHESTPLATE),
            ItemStack(Material.GOLDEN_LEGGINGS),
            ItemStack(Material.BREAD, 10),
            ItemStack(Material.TORCH, 10)
    )

    // 3-4
    private val respawnFour = listOf(
            ItemStack(Material.IRON_AXE),
            ItemStack(Material.IRON_PICKAXE),
            ItemStack(Material.STONE_SHOVEL),
            ItemStack(Material.STONE_SWORD),
            ItemStack(Material.IRON_HELMET),
            ItemStack(Material.IRON_BOOTS),
            ItemStack(Material.FURNACE),
            ItemStack(Material.COAL, 3),
            ItemStack(Material.OAK_LOG, 10)
    )

    // 4-5
    private val respawnFive = listOf(
            ItemStack(Material.IRON_AXE),
            ItemStack(Material.IRON_PICKAXE),
            ItemStack(Material.STONE_SHOVEL),
            ItemStack(Material.STONE_SWORD),
            ItemStack(Material.IRON_HELMET),
            ItemStack(Material.IRON_BOOTS),
            ItemStack(Material.GOLDEN_CHESTPLATE),
            ItemStack(Material.GOLDEN_LEGGINGS),
            ItemStack(Material.FURNACE),
            ItemStack(Material.COAL, 3),
            ItemStack(Material.OAK_LOG, 2),
            ItemStack(Material.COOKED_BEEF, 5)
    )

    // 5-6
    private val respawnSix = listOf(
            ItemStack(Material.IRON_AXE),
            ItemStack(Material.IRON_PICKAXE),
            ItemStack(Material.STONE_SHOVEL),
            ItemStack(Material.STONE_SWORD),
            ItemStack(Material.IRON_HELMET),
            ItemStack(Material.IRON_BOOTS),
            ItemStack(Material.GOLDEN_CHESTPLATE),
            ItemStack(Material.IRON_LEGGINGS),
            ItemStack(Material.FURNACE),
            ItemStack(Material.COAL, 3),
            ItemStack(Material.OAK_LOG, 2),
            ItemStack(Material.COOKED_BEEF, 10)
    )

    // 6-7
    private val respawnSeven = listOf(
            ItemStack(Material.IRON_AXE),
            ItemStack(Material.IRON_PICKAXE),
            ItemStack(Material.STONE_SHOVEL),
            ItemStack(Material.STONE_SWORD),
            ItemStack(Material.IRON_HELMET),
            ItemStack(Material.IRON_BOOTS),
            ItemStack(Material.IRON_CHESTPLATE),
            ItemStack(Material.IRON_LEGGINGS),
            ItemStack(Material.FURNACE),
            ItemStack(Material.COAL, 3),
            ItemStack(Material.OAK_LOG, 2),
            ItemStack(Material.COOKED_BEEF, 5)
    )

    // 7-8
    private val respawnEight = listOf(
            ItemStack(Material.IRON_AXE),
            ItemStack(Material.IRON_PICKAXE),
            ItemStack(Material.STONE_SHOVEL),
            ItemStack(Material.STONE_SWORD),
            ItemStack(Material.IRON_HELMET),
            ItemStack(Material.IRON_BOOTS),
            ItemStack(Material.IRON_CHESTPLATE),
            ItemStack(Material.IRON_LEGGINGS),
            ItemStack(Material.FURNACE),
            ItemStack(Material.COAL, 3),
            ItemStack(Material.OAK_LOG, 2),
            ItemStack(Material.COOKED_BEEF, 10),
            ItemStack(Material.SHIELD)
    )

    // 8-9
    private val respawnNine = listOf(
            ItemStack(Material.IRON_AXE),
            ItemStack(Material.IRON_PICKAXE),
            ItemStack(Material.STONE_SHOVEL),
            ItemStack(Material.STONE_SWORD),
            ItemStack(Material.IRON_HELMET),
            ItemStack(Material.IRON_BOOTS),
            ItemStack(Material.IRON_CHESTPLATE),
            ItemStack(Material.IRON_LEGGINGS),
            ItemStack(Material.FURNACE),
            ItemStack(Material.COAL, 3),
            ItemStack(Material.OAK_LOG, 2),
            ItemStack(Material.COOKED_BEEF, 15),
            ItemStack(Material.SHIELD),
            ItemStack(Material.BOW),
            ItemStack(Material.ARROW, 10)
    )

    private val tierOne = listOf(
            ItemStack(Material.IRON_ORE, random.nextInt(5) + 1),
            ItemStack(Material.BREAD, random.nextInt(5) + 5)
    )

    private val tierTwo = listOf(
            ItemStack(Material.IRON_HELMET),
            ItemStack(Material.IRON_BOOTS),
            ItemStack(Material.IRON_ORE, random.nextInt(5) + 1),
            ItemStack(Material.BREAD, random.nextInt(5) + 5),
            ItemStack(Material.COAL, random.nextInt(5) + 5),
            ItemStack(Material.OAK_LOG, random.nextInt(3) + 5)
    )

    private val tierThree = listOf(
            ItemStack(Material.BREAD, random.nextInt(5) + 1),
            ItemStack(Material.COAL, random.nextInt(10) + 1),
            ItemStack(Material.OAK_LOG, random.nextInt(8) + 5),
            ItemStack(Material.IRON_ORE, random.nextInt(5) + 1),
            ItemStack(Material.TORCH, 32)
    )

    private val tierFour = listOf(
            ItemStack(Material.BREAD, random.nextInt(5) + 1),
            ItemStack(Material.COAL, random.nextInt(10) + 1),
            ItemStack(Material.OAK_LOG, random.nextInt(5) + 1),
            ItemStack(Material.IRON_ORE, random.nextInt(5) + 1),
            ItemStack(Material.ENDER_PEARL)
    )

    private val tierFive = listOf(
            ItemStack(Material.ENDER_PEARL),
            ItemStack(Material.DIAMOND, random.nextInt(2))
    )

    private val respawnLevel = listOf(
            respawnOne,
            respawnTwo,
            respawnThree,
            respawnFour,
            respawnFive,
            respawnSix,
            respawnSeven,
            respawnEight,
            respawnNine
    )

    private val effectTier = listOf(
            tierOne,
            tierTwo,
            tierThree,
            tierFour,
            tierFive
    )

    /**
     * Generates a list of ItemStacks to be added to the inventory
     *
     * Generates standard items based on the game score, and a chance at
     * extra items if the game score's based on the game score's decimals
     *
     * @return a list of ItemStacks
     */
    fun generateInventory(): List<ItemStack> {
        calculateScore(main.gameState)
        val mainItems = mutableListOf<ItemStack>()
        mainItems.addAll(respawnLevel[level])
        val addItems = Math.random() < modifier
        val extraItems = mutableListOf<ItemStack>()
        if (addItems) {
            val activeTier: List<ItemStack> = if (level == 0) {
                effectTier[0]
            } else {
                effectTier[level - 1]
            }
            for (itemStack in activeTier) {
                if (Math.random() < modifier) {
                    extraItems.add(itemStack)
                }
            }
        }
        mainItems.addAll(extraItems)
        return mainItems
    }

    /**
     * Calculates the score
     *
     * @param score
     */
    private fun calculateScore(score: Double) {
        level = score.toInt()
        val scoreAsString = java.lang.Double.toString(score)
        val decimalString = scoreAsString.substring(scoreAsString.indexOf("."))
        modifier = getDecimal(decimalString)
    }

    /**
     * Gets the decimal value in the game score
     *
     * @param s game score
     * @return the decimal value
     */
    private fun getDecimal(s: String): Double {
        val fullDecimal = "0$s"
        return fullDecimal.toDouble()
    }
}