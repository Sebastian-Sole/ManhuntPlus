package com.yoonicode.minecraftmanhuntplus

import org.bukkit.inventory.Inventory
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.Material
import org.bukkit.inventory.meta.SkullMeta

/**
 * This handles the inventory of a compass
 *
 * @property main the main plugin
 */
class TargetSelectInventory(var main: PluginMain) {
    private var inventory: Inventory = Bukkit.createInventory(null, 9, INVENTORY_NAME)

    init {
        for ((pos, runner) in main.runners.withIndex()) {
            val stack = ItemStack(Material.PLAYER_HEAD, 1)
            val meta = stack.itemMeta as SkullMeta?
            meta?.owningPlayer = runner
            meta?.setDisplayName(runner.name)
            val easteregg = main.config.getString(runner.name, "")!!
            if (easteregg.isNotEmpty()) meta?.lore = mutableListOf(easteregg)
            stack.itemMeta = meta
            inventory.setItem(pos, stack)
        }
    }

    fun displayToPlayer(player: Player) {
        player.openInventory(inventory)
    }

    companion object {
        const val INVENTORY_NAME = "Select player to track"
    }
}