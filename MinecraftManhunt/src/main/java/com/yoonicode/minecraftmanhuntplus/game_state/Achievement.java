package com.yoonicode.minecraftmanhuntplus.game_state;

public enum Achievement {
    SMELT_IRON(2),
    OBTAIN_ARMOR(3),
    MINE_DIAMOND(4),
    ENTER_THE_NETHER(5), // NETHER
    FIND_FORTRESS(6), // A TERRIBLE FORTRESS
    FIND_BASTION(6), // WAR PIGS
    DISTRACT_PIGLIN(7), // OH SHINY
    OBTAIN_BLAZE_ROD(7), // INTO FIRE
    OBTAIN_ANCIENT_DEBRIS(9), // HIDDEN DEPTHS
    EYE_THROW(9), // EYETHROW
    FOLLOW_ENDER_EYE(10), // STRONGHOLD
    ENTER_THE_END(12);


    private int achivementLevel;

    Achievement(int i) {
        this.achivementLevel = i;
    }

    public int getAchivementLevel() {
        return achivementLevel;
    }


}
