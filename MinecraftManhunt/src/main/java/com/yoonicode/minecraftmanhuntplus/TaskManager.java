package com.yoonicode.minecraftmanhuntplus;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class TaskManager {


    private final PluginMain main;

    public TaskManager(PluginMain main) {
        this.main = main;
    }

    public void updateCompass(){
        for(Map.Entry<String, String> i : main.targets.entrySet()){
            Player hunter = Bukkit.getPlayer(i.getKey());
            Player target = Bukkit.getPlayer(i.getValue());
            if(hunter == null || target == null){
                continue;
            }
            if(!main.playerIsOnTeam(hunter)){
                continue;
            }
            PlayerInventory inv = hunter.getInventory();

            if(hunter.getWorld().getEnvironment() != target.getWorld().getEnvironment()){
                Location loc = main.portals.get(target.getName());
                if(loc != null){
                    hunter.setCompassTarget(loc);
                }
                for (int j = 0; j < inv.getSize(); j++) {
                    ItemStack stack = inv.getItem(j);
                    if (stack == null) continue;
                    if (stack.getType() != Material.COMPASS) continue;

                    stack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1); // Make all compasses glow

                    ItemMeta meta = stack.getItemMeta();
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    stack.setItemMeta(meta);
                }
            }else{
                hunter.setCompassTarget(target.getLocation());

                if(main.compassEnabledInNether) {
                    for (int j = 0; j < inv.getSize(); j++) {
                        ItemStack stack = inv.getItem(j);
                        if (stack == null) continue;
                        if (stack.getType() != Material.COMPASS) continue;

                        CompassMeta meta = (CompassMeta) stack.getItemMeta();
                        meta.setLodestone(target.getLocation());
                        meta.setLodestoneTracked(false);
                        stack.setItemMeta(meta);
                    }
                }
            }
        }
    }

    public void giveHaste() {
        for (Player player : main.hunters){
            player.getPlayer().addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(Integer.MAX_VALUE, 3));
        }
        for (Player player : main.runners){
            player.getPlayer().addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(Integer.MAX_VALUE, 3));
        }
    }



}
