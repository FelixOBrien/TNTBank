package com.felixob.tb.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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
		this.optionalArgs.put("#", "amount/radius");
		this.optionalArgs.put("fill", "amount");


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
				return;
			}else if(command.equals("check")) {
				Common.tell(p, "&aThere is " + cache.tntCount(context.faction.getId()) + " TNT in the Bank");
				return;
			}else if(command.equals("fill")) {
				Common.tell(p, Settings.noRadius);
				return;
			}else{
				Common.tell(p, Settings.unknownDirection);
				return;

			}

		}

		if(args.size() >= 2) {
			int amount = 0;
			HashMap<Integer, ? extends ItemStack> ting = p.getInventory().all(Material.TNT);
			boolean all = false;
			if(args.get(1).equals("all") || args.get(1).equals("*") || args.get(1).equals("max")) {
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
				int limit = freeSpaceInv(p.getInventory());
				limit -= 64*5;

				if(limit == 0) {
					Common.tell(p, Settings.noFreeSpace);
					return;
				}
				if(limit > cache.tntCount(context.faction.getId())) {
					limit = cache.tntCount(context.faction.getId());
				}
				if(all) {
					cache.withdrawTNT(context.faction.getId(), limit, p);
					p.getInventory().addItem(new ItemStack(Material.TNT, limit));
					return;
				}else {
					if(amount <= 0) {
						Common.tell(p, "&cYou need to withdraw more than 0 TNT");
						return;
					}
					if(amount > cache.tntCount(context.faction.getId())) {
						Common.tell(p, Settings.notEnoughTNT.replaceAll("%current_tnt%", "" + cache.tntCount(context.faction.getId())));
						return;
					}
					cache.withdrawTNT(context.faction.getId(), amount, p);
					p.getInventory().addItem(new ItemStack(Material.TNT, amount));
					return;
				}

			}else if(args.get(0).equals("deposit")) {
				if(cache.tntCount(context.faction.getId()) >= Settings.tntLimit) {
					Common.tell(p, Settings.tntLimitReached.replaceAll("%tnt_limit%", Settings.tntLimit + ""));
					return;
				}
				if(amount <1) {
					Common.tell(p, "&cYou do not have any TNT");
					return;
				}
				if(all) {


					p.getInventory().remove(Material.TNT);
					cache.depositTNT(context.faction.getId(), amount, p);
					return;
				}else {
					int totalTnt = 0;
					for(Entry<Integer, ? extends ItemStack> i : ting.entrySet()) {
						totalTnt += i.getValue().getAmount();
					}
					if(amount > totalTnt) {
						
						Common.tell(p, Settings.notEnoughTNTInv.replaceAll("%current_tnt%", amount + ""));
						return;
					}
					p.getInventory().removeItem(new ItemStack(Material.TNT, amount));
					cache.depositTNT(context.faction.getId(), amount, p);
					return;
				}

			}else if(args.get(0).equals("fill")) {
				if(cache.tntCount(context.faction.getId()) <1) {
					Common.tell(p, "&cYou do not have any TNT");
					return;
				}
				int radius = 0;
				if(all) {
					radius = 32;
				}else {
					if(amount > 0) {
						radius = amount;
					}else {
						Common.tell(p, Settings.smallRadius);
						return;
					}
				}

				Location loc = p.getLocation();
				World world = loc.getWorld();
				Map<Chest, Integer> chests = new HashMap<Chest, Integer>();
				Map<Dispenser, Integer> dispensers = new HashMap<Dispenser, Integer>();
				int slotsToFill = 0;
				for (int x = -radius; x < radius; x++) {
					for (int y = -radius; y < radius; y++) {
						for (int z = -radius; z < radius; z++) {
							Block block = world.getBlockAt(loc.getBlockX()+x, loc.getBlockY()+y, loc.getBlockZ()+z);
							if (block.getType() == Material.DISPENSER) {
								Dispenser dispenser = (Dispenser) block.getState();
								int freeSpace = freeSpaceInv(dispenser.getInventory());
								slotsToFill += freeSpace;

								dispensers.put(dispenser, freeSpace);
							}else if(block.getType() == Material.CHEST) {
								Chest chest = (Chest) block.getState();
								int freeSpace = freeSpaceInv(chest.getInventory());
								slotsToFill += freeSpace;
								chests.put(chest, freeSpace);

							}
						}
					}
				}
				int objectCount = chests.size() + dispensers.size();
				if(objectCount <1) {
					Common.tell(p, Settings.noContainers);
					return;
				}
				int currentTNT = cache.tntCount(context.faction.getId());
				if(args.get(2) != null) {
					int perContainer = Integer.parseInt(args.get(2));
					if(perContainer <= 0) {
						Common.tell(p, Settings.negativeTNT);
						return;
					}
					int total = perContainer * objectCount;
					if(total > currentTNT) {
						int tntPerObject = (int) Math.floor(currentTNT/objectCount);
						int remainder = currentTNT - (tntPerObject * objectCount);
						for (Map.Entry<Chest, Integer> entry : chests.entrySet()) {
							entry.getKey().getInventory().addItem(new ItemStack(Material.TNT, tntPerObject));
						}
						for (Map.Entry<Dispenser, Integer> entry : dispensers.entrySet()) {
							entry.getKey().getInventory().addItem(new ItemStack(Material.TNT, tntPerObject));
						}
						if(remainder > 0) {
							if(chests.size() > 0) {
								Entry<Chest, Integer> entry = chests.entrySet().iterator().next();
								entry.getKey().getInventory().addItem((new ItemStack(Material.TNT, remainder)));
							}else {
								Entry<Dispenser, Integer> entry = dispensers.entrySet().iterator().next();
								entry.getKey().getInventory().addItem((new ItemStack(Material.TNT, remainder)));
							}
							cache.withdrawTNT(context.faction.getId(), currentTNT, p);
							Common.tell(p, Settings.successFill.replaceAll("%filled_slots%", currentTNT + ""));
							return;
						}

					}else {
						for (Map.Entry<Chest, Integer> entry : chests.entrySet()) {
							entry.getKey().getInventory().addItem(new ItemStack(Material.TNT, perContainer));
						}
						for (Map.Entry<Dispenser, Integer> entry : dispensers.entrySet()) {
							entry.getKey().getInventory().addItem(new ItemStack(Material.TNT, perContainer));
						}
						cache.withdrawTNT(context.faction.getId(), total, p);
						Common.tell(p, Settings.successFill.replaceAll("%filled_slots%", total + ""));
						return;
					}

				}
				if(slotsToFill > currentTNT) {
					int tntPerObject = (int) Math.floor(currentTNT/objectCount);
					int remainder = currentTNT - (tntPerObject * objectCount);
					for (Map.Entry<Chest, Integer> entry : chests.entrySet()) {
						entry.getKey().getInventory().addItem(new ItemStack(Material.TNT, tntPerObject));
					}
					for (Map.Entry<Dispenser, Integer> entry : dispensers.entrySet()) {
						entry.getKey().getInventory().addItem(new ItemStack(Material.TNT, tntPerObject));
					}
					if(remainder > 0) {
						if(chests.size() > 0) {
							Entry<Chest, Integer> entry = chests.entrySet().iterator().next();
							entry.getKey().getInventory().addItem((new ItemStack(Material.TNT, remainder)));
						}else {
							Entry<Dispenser, Integer> entry = dispensers.entrySet().iterator().next();
							entry.getKey().getInventory().addItem((new ItemStack(Material.TNT, remainder)));
						}
					}
					slotsToFill = currentTNT;


				}else {
					for (Map.Entry<Chest, Integer> entry : chests.entrySet()) {
						entry.getKey().getInventory().addItem(new ItemStack(Material.TNT, entry.getValue()));
					}
					for (Map.Entry<Dispenser, Integer> entry : dispensers.entrySet()) {
						entry.getKey().getInventory().addItem(new ItemStack(Material.TNT, entry.getValue()));
					}

				}
				cache.withdrawTNT(context.faction.getId(), slotsToFill, p);
				Common.tell(p, Settings.successFill.replaceAll("%filled_slots%", slotsToFill + ""));
				return;
			}
			Common.tell(p, Settings.unknownDirection);
			return;
		}

		return;
	}

	public static int freeSpaceInv(Inventory inv) {
		int limit = 0;
		for(ItemStack i : inv.getContents()) {
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
		return limit;
	}




}
