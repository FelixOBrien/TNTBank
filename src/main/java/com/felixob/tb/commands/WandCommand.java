package com.felixob.tb.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.felixob.tb.config.Settings;
import com.felixob.tb.utils.Common;



public class WandCommand implements CommandExecutor{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
			if(!(sender.hasPermission("tntbank.wand"))) {
				Common.tell(sender, Settings.noPermission);
				return true;
			}
			if(args.length < 2) {
				return true;
			}
			Player p = (Player) Bukkit.getPlayerExact(args[1]);
			if(p == null) {
				Common.tell(sender, "&cPlayer doesn't exist");
				return true;
			}
			ItemStack stick = new ItemStack(Material.STICK, 1);
			if(stick.hasItemMeta()) {
				ItemMeta meta = stick.getItemMeta();
				List<String> lore = meta.getLore();
				List<String> newLore = lore;
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Settings.stickName));
				newLore.add("TNT Stick");
				meta.setLore(newLore);
				stick.setItemMeta(meta);
				p.getInventory().addItem(stick);
				Common.tell(p, Settings.stick);
				return true;
			}else {

				List<String> newLore = new ArrayList<String>();
				ItemMeta meta = stick.getItemMeta();
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Settings.stickName));
				newLore.add("TNT Stick");
				meta.setLore(newLore);
				stick.setItemMeta(meta);
				p.getInventory().addItem(stick);
				Common.tell(p, Settings.stick);
				return true;
			}
	}

}
