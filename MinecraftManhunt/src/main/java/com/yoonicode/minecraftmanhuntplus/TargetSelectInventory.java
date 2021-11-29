package com.yoonicode.minecraftmanhuntplus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class TargetSelectInventory {

    public static final String INVENTORY_NAME = "Select player to track";

    Inventory inv;
    PluginMain main;


    public TargetSelectInventory(PluginMain main){
        this.main = main;
        inv = Bukkit.createInventory(null, 9, INVENTORY_NAME);
        int pos = 0;
        for(Player runner : main.runners){
            if(runner == null) continue;
            ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            meta.setOwningPlayer(runner);
            meta.setDisplayName(runner.getName());

            String easteregg = main.getConfig().getString(runner.getName(), "");
            if(easteregg.length() > 0) meta.setLore(Arrays.asList(easteregg));
            stack.setItemMeta(meta);

            inv.setItem(pos, stack);
            pos++;
        }
    }

    public Inventory getInventory() {
        return inv;
    }

    public void DisplayToPlayer(Player player){
        player.openInventory(inv);
    }

}
