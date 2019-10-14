package com.felixob.tb;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import com.felixob.tb.config.Settings;
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

	public FactionCache getCache() {
		return cache;
	}
}
