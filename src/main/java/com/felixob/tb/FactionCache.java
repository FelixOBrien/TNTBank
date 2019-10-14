package com.felixob.tb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.felixob.tb.utils.Common;


public class FactionCache {

	private TNTBank plugin;
	private File file = null;
	private Map<String, Integer> map = new HashMap<String, Integer>();

	public FactionCache(TNTBank plugin)
	{
		this.plugin = plugin;
		this.map  = new HashMap<String, Integer>();
		this.init();
	}

	public void init()
	{
		File dataFolder = this.plugin.getDataFolder();
		file = new File(dataFolder, "cache.dat");

		if(file.exists()) {
			ObjectInputStream input;
			try {
				input = new ObjectInputStream(new FileInputStream(file));
				Object readObject = input.readObject();
				input.close();

				map = (Map<String, Integer>) readObject;
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	public int tntCount(String id) {
		if(map.get(id) != null) {
			return map.get(id);
		}else {
			depositTNT(id, 0);
			return map.get(id);
		}
	}
	public void depositTNT(String id, int amount) {
		if(map.get(id) == null) {
	
			map.put(id, amount);
		}
		int tnt = map.get(id);
		map.put(id, amount + tnt);

		try {
			if(file.exists()) {
				file.delete();
			}
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		save();
	}
	public void withdrawTNT(String id, int amount) {
		map.put(id, tntCount(id) - amount);
		save();
	}
	public Map<String, Integer> getMap(){
		return map;
	}

	public void save() {
		ObjectOutputStream output;
		try {
			output = new ObjectOutputStream(new FileOutputStream(file));
			output.writeObject(map);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
;
	}
	public FactionCache getCache() {
		Common.log("Got Instance");
		return this;
	}
}
