package com.yoonicode.minecraftmanhuntplus.game_state

/**
 * TODO
 *
 * @property achievementLevel
 */
enum class AdvancementValue(val achievementLevel: Int) {
    SMELT_IRON(2), // Gets an iron ingot
    OBTAIN_ARMOR(3), // Gets iron armor
    MINE_DIAMOND(5), // Mines a diamond
    ENTER_THE_NETHER(7),  // NETHER
    FIND_FORTRESS(8),  // A TERRIBLE FORTRESS
    FIND_BASTION(8),  // WAR PIGS
    DISTRACT_PIGLIN(9),  // OH SHINY
    OBTAIN_BLAZE_ROD(9),  // INTO FIRE
    OBTAIN_ANCIENT_DEBRIS(10),  // HIDDEN DEPTHS
    EYE_THROW(10),  // EYETHROW
    FOLLOW_ENDER_EYE(11),  // STRONGHOLD
    ENTER_THE_END(13); // Enters the nether

}