package com.felixob.tb.config;

import com.felixob.tb.config.SimpleConfig;

public class Settings extends SimpleConfig{
	private Settings(String fileName) {
		super(fileName);
	}
	
	public static String factionDoesntExist;
	public static String generalError;
	public static String noPermission;
	public static String notAPlayer;
	public static String notAnArgument;
	public static String noAmountProvided;
	public static String unknownDirection;
	public static String noFreeSpace;
	public static String withdrawMessage;
	public static String notEnoughTNT;
	public static String notEnoughTNTInv;
	public static String negativeTNT;
	public static String smallRadius;
	public static String noRadius;
	public static String noContainers;
	public static String noTNT;
	public static String stick;
	public static String currentTNT;
	public static String successFill;
	public static String depositMessage;
	public static int tntLimit;
	public static String tntLimitReached;
	public static String stickName;
	public static String stickNoTNT;
	
	private void onLoad() {
		factionDoesntExist = getString("factionDoesntExist");
		generalError = getString("generalError");
		noPermission = getString("noPermission");
		notAPlayer = getString("notAPlayer");
		notAnArgument = getString("notAnArgument");
		noAmountProvided = getString("noAmountProvided");
		unknownDirection = getString("unknownDirection");
		noFreeSpace = getString("noFreeSpace");
		withdrawMessage = getString("withdrawMessage");
		notEnoughTNT = getString("notEnoughTNT");
		notEnoughTNTInv = getString("notEnoughTNTInv");
		negativeTNT = getString("negativeTNT");
		smallRadius = getString("smallRadius");
		noRadius = getString("noRadius");
		noContainers = getString("noContainers");
		noTNT = getString("noTNT");
		stick = getString("stick");
		currentTNT = getString("currentTNT");
		successFill = getString("successFill");
		depositMessage = getString("depositMessage");
		tntLimit = getInt("tntLimit");
		tntLimitReached = getString("tntLimitReached");
		stickName = getString("stickName");
		stickNoTNT = getString("stickNoTNT");
	}
	public static void init() {
		new Settings("settings.yml").onLoad();
	}
}