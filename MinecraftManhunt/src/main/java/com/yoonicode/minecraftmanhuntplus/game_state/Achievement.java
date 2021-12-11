package com.yoonicode.minecraftmanhuntplus.game_state;

public enum Achievement {
    SMELT_IRON(2),
    OBTAIN_ARMOR(3),
    MINE_DIAMOND(5),
    ENTER_THE_NETHER(7), // NETHER
    FIND_FORTRESS(8), // A TERRIBLE FORTRESS
    FIND_BASTION(8), // WAR PIGS
    DISTRACT_PIGLIN(9), // OH SHINY
    OBTAIN_BLAZE_ROD(9), // INTO FIRE
    OBTAIN_ANCIENT_DEBRIS(10), // HIDDEN DEPTHS
    EYE_THROW(10), // EYETHROW
    FOLLOW_ENDER_EYE(11), // STRONGHOLD
    ENTER_THE_END(13);


    private int achivementLevel;

    Achievement(int i) {
        this.achivementLevel = i;
    }

    public int getAchivementLevel() {
        return achivementLevel;
    }


}
