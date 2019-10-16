package com.felixob.tb;


import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.block.Container;
import com.felixob.tb.commands.FTNT;
import com.felixob.tb.commands.WandCommand;
import com.felixob.tb.config.Settings;
import com.felixob.tb.utils.Common;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class TNTBank extends JavaPlugin implements Listener{
	private static TNTBank instance;
	private FactionCache cache;
	@Override
	public void onEnable() {
		getLogger().info("TNTBank has been enabled."); 
		instance = this;
		Settings.init();
		TL.inject();
		Permission.inject();
		this.getCommand("tntbank").setExecutor(new WandCommand());
		this.getServer().getPluginManager().registerEvents(this, this);
		cache = new FactionCache(this);
	}
	@Override
	public void onDisable() {
		getLogger().info("TNTBank has been disabled."); 
		instance = null;
	}
	public static TNTBank getInstance() {
		return instance;
	}



	@EventHandler
	public void pluginEnableEvent(PluginEnableEvent e) {
		if(e.getPlugin().getName().equals("Factions")) {
			CommandInjector.injectCommand();
			this.getServer().getPluginManager().registerEvents(new DisbandListener(), this);

		}


	}
	@EventHandler
	public void blockInteractEvent(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getItem() != null && e.getItem().getItemMeta() != null && e.getItem().getItemMeta().getLore() != null) {
			if(!(e.getItem().hasItemMeta())) {
				return;
			}
			List<String> lore = e.getItem().getItemMeta().getLore();
			if(e.getClickedBlock() == null) {
				return;
			}
			if(lore == null) {
				return;
			}
			if(!(e.getClickedBlock().getState() instanceof Container)) {
				return;
			}
			if(e.getAction() != Action.LEFT_CLICK_BLOCK) {
				return;
			}

			for(String f : lore) {
				if(f.toLowerCase().contains("tnt stick")) {
					int freeSpace = 0;
					for(ItemStack i : ((Container) e.getClickedBlock().getState()).getInventory()) {
						if(i != null && i.getType() != null) {
							if(i.getType() == Material.TNT) {
								freeSpace += i.getAmount();
							}
						}
					}
					if(freeSpace <= 0) {
						Common.tell(p, Settings.stickNoTNT);
						return;
					}
					FPlayer Fp = FPlayers.getInstance().getByPlayer(e.getPlayer());
					if(Fp.getFaction() != null) {
						String id = Fp.getFactionId();
						if(cache.tntCount(id) >= Settings.tntLimit) {
							Common.tell(p, Settings.tntLimitReached);
							return;
						}
						cache.depositTNT(id, freeSpace, p);
						((Container) e.getClickedBlock().getState()).getInventory().remove(Material.TNT);
					}else {
						Common.tell(p, Settings.factionDoesntExist);
						return;
					}
				}
			}
		}
	}

	public FactionCache getCache() {
		return cache;
	}
}
