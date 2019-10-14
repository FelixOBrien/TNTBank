package com.felixob.tb.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.felixob.tb.FactionCache;
import com.felixob.tb.TNTBank;
import com.felixob.tb.config.Settings;
import com.felixob.tb.utils.Common;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class FTNT extends FCommand{

	public FTNT () {
		super();
		this.aliases.add("tnt");

		this.requiredArgs.add("type");
		this.optionalArgs.put("#", "amount");

		this.requirements = new CommandRequirements.Builder(Permission.TNT_BANK).memberOnly().noDisableOnLock().build();
	}


	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_TNT_BANK_DESCRIPTION;

	}

	@Override
	public void perform(CommandContext context) {
		Player p = context.player;
		List<String> args = context.args;
		FactionCache cache = TNTBank.getInstance().getCache();
		if(args.size() == 1) {
			String command = args.get(0).toLowerCase();
			if(command.equals("withdraw") || command.equals("deposit")) { 
				Common.tell(p, Settings.noAmountProvided);
			}else if(command.equals("check")) {
				Common.tell(p, "&aThere is " + cache.tntCount(context.faction.getId()) + " TNT in the Bank");
			}else{
				Common.tell(p, Settings.unknownDirection);
			}
			return;

		}

		if(args.size() == 2) {
			int amount = 0;
			HashMap<Integer, ? extends ItemStack> ting = p.getInventory().all(Material.TNT);
			boolean all = false;
			if(args.get(1).equals("all") || args.get(1).equals("*")) {
				all = true;
				for(Entry<Integer, ? extends ItemStack> i : ting.entrySet()) {
					amount += i.getValue().getAmount();
				}
			}else{
				try {
					amount = Integer.parseInt(args.get(1));
				} catch(NumberFormatException e){
					amount = 0;
				}
			}
			if(amount < 0) {
				Common.tell(p, Settings.negativeTNT);
				return;
			}
			if(args.get(0).equals("withdraw")) {
				int limit = 0;
				for(ItemStack i : p.getInventory().getContents()) {
					if(i == null) {
						limit += 64;
					}else if(!(i.getType() == null) && i.getType().equals(Material.TNT)) {
						//There is TNT in the inventory
						limit += i.getMaxStackSize() - i.getAmount();
					}else if(i.getType().equals(Material.AIR)) {
						//Empty Slots = FREE REAL ESTATE
						limit += i.getMaxStackSize();
					}

				}
				limit -= 64*5;

				if(limit == 0) {
					Common.tell(p, Settings.noFreeSpace);
					return;
				}
				if(all) {
					Common.tell(p, "Withdrawing " + limit + " TNT");
					cache.withdrawTNT(context.faction.getId(), limit);
					p.getInventory().addItem(new ItemStack(Material.TNT, limit));
					Common.tell(p, Settings.withdrawMessage);
					return;
				}else {
					if(amount <= 0) {
						Common.tell(p, "&cYou need to withdraw more than 0 TNT");
						return;
					}
					if(amount > cache.tntCount(context.faction.getId())) {
						Common.tell(p, Settings.notEnoughTNT + " Bank has: " + cache.getCache().tntCount(context.faction.getId()) + " TNT");
						return;
					}
					cache.withdrawTNT(context.faction.getId(), amount);
					p.getInventory().addItem(new ItemStack(Material.TNT, amount));

					Common.tell(p, Settings.withdrawMessage);
					Common.tell(p, "&aBank now has " + cache.tntCount(context.faction.getId()) + " TNT.");

					return;
				}

			}else if(args.get(0).equals("deposit")) {
				if(amount <1) {
					Common.tell(p, "&cYou do not have any TNT");
					return;
				}
				if(all) {


					p.getInventory().remove(Material.TNT);
					cache.getCache().depositTNT(context.faction.getId(), amount);
					Common.tell(p, "&a" + amount + " TNT Deposited");
					return;
				}else {
					int totalTnt = 0;
					for(Entry<Integer, ? extends ItemStack> i : ting.entrySet()) {
						totalTnt += i.getValue().getAmount();
					}
					if(amount > totalTnt) {
						Common.tell(p, Settings.notEnoughTNTInv + " You have: "+ amount + " TNT");
						return;
					}
					p.getInventory().removeItem(new ItemStack(Material.TNT, amount));
					cache.getCache().depositTNT(context.faction.getId(), amount);
					Common.tell(p, "&a" + amount + " TNT Deposited");
					return;
				}

			}
			Common.tell(p, Settings.unknownDirection);
			return;
		}

		return;

	}






}
