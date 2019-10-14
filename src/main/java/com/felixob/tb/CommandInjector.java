package com.felixob.tb;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import com.felixob.tb.commands.FTNT;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.FCmdRoot;

public final class CommandInjector {
	
	private CommandInjector() {
		
	}
	public static void injectCommand() {
		FTNT tnt = new FTNT();
		 FCmdRoot root = FCmdRoot.getInstance();
		root.addSubCommand(tnt);
		
		
		CommandContext context = new CommandContext(Bukkit.getConsoleSender(), new ArrayList<String>(), "help");
		root.cmdHelp.updateHelp(context);
		root.cmdHelp.helpPages.get(0).add(tnt.getUsageTemplate(context, true));
	}

}
