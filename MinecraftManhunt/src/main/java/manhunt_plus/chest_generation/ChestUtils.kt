package manhunt_plus.chest_generation
import org.bukkit.inventory.ItemStack
import java.util.*

val random = Random()

/**
 * Generates items to add in a chest
 *
 * @return an array list of item stacks with enchantments (if enchantable) to be added to the chest
 */
fun generateChestItems(): MutableList<ItemStack> {
    val itemGenerator = ItemGenerator()

    val itemsToAdd = mutableListOf<ItemStack>()
    val numberOfItemsToAdd = random.nextInt(6) + 5 // At least 5, max 10
    for (i in 0 until numberOfItemsToAdd) { //Generate that amount of random items
        when (random.nextInt(28) + 1) {
            1, 2 -> itemGenerator.generateChestItemStack(0, "chest")?.let { itemsToAdd.add(it) }
            3, 4, 5, 6, 7 -> itemGenerator.generateChestItemStack(1, "chest")?.let { itemsToAdd.add(it) }
            8, 9, 10, 11, 12, 13 -> itemGenerator.generateChestItemStack(2, "chest")?.let { itemsToAdd.add(it) }
            14, 15, 16, 17, 18, 19, 20 -> itemGenerator.generateChestItemStack(3, "chest")?.let { itemsToAdd.add(it) }
            21, 22, 23, 24, 25, 26, 27, 28 -> itemGenerator.generateChestItemStack(4, "chest")?.let { itemsToAdd.add(it) }
        }
    }
    return itemsToAdd
}

fun generateSupplyDropItems(): MutableList<ItemStack> {
    val itemGenerator = ItemGenerator()

    val itemsToAdd = mutableListOf<ItemStack>()
    for (i in 0 until 5) { //Generate that amount of random items
        when (random.nextInt(11) + 1) {
            1, 2 -> itemGenerator.generateChestItemStack(0, "supplyDrop")?.let { itemsToAdd.add(it) }
            3, 4, 5, 6 -> itemGenerator.generateChestItemStack(1, "supplyDrop")?.let { itemsToAdd.add(it) }
            8, 9, 10, 11 -> itemGenerator.generateChestItemStack(2, "supplyDrop")?.let { itemsToAdd.add(it) }
        }
    }
    return itemsToAdd
}