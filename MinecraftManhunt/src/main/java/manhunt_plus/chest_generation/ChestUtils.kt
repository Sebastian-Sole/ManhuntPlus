package manhunt_plus.chest_generation

import org.bukkit.inventory.ItemStack
import java.util.*

val random = Random()

/**
 * Generates items to add in a chest
 *
 * @return an array list of item stacks with enchantments (if enchantable) to be added to the chest
 */
fun generateItems(): MutableList<ItemStack> {
    val itemGenerator = ItemGenerator()

    val itemsToAdd = mutableListOf<ItemStack>()
    val numberOfItemsToAdd = random.nextInt(6) + 5 // At least 5, max 10
    for (i in 0 until numberOfItemsToAdd) { //Generate that amount of random items
        when (random.nextInt(28) + 1) {
            1, 2 -> itemGenerator.generateItemStack(0)?.let { itemsToAdd.add(it) }
            3, 4, 5, 6, 7 -> itemGenerator.generateItemStack(1)?.let { itemsToAdd.add(it) }
            8, 9, 10, 11, 12, 13 -> itemGenerator.generateItemStack(2)?.let { itemsToAdd.add(it) }
            14, 15, 16, 17, 18, 19, 20 -> itemGenerator.generateItemStack(3)?.let { itemsToAdd.add(it) }
            21, 22, 23, 24, 25, 26, 27, 28 -> itemGenerator.generateItemStack(4)?.let { itemsToAdd.add(it) }
        }
    }
    return itemsToAdd
}