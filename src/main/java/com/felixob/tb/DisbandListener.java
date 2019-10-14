package com.felixob.tb;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.felixob.tb.utils.Common;
import com.massivecraft.factions.event.FactionDisbandEvent;

public class DisbandListener implements Listener{
	
	@EventHandler
	public void factionDisband(FactionDisbandEvent e) {
		Common.log("Faction Disband Event");
		TNTBank.getInstance().getCache().getMap().remove(e.getFaction().getId());
	}
}
