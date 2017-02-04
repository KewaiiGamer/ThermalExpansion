package cofh.thermalexpansion.init;

import cofh.api.core.IInitializer;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.ItemUpgrade;

import java.util.ArrayList;

public class TEItems {

	private TEItems() {

	}

	public static void preInit() {

		itemUpgrade = new ItemUpgrade();

		initList.add(itemUpgrade);

		ThermalExpansion.proxy.addIModelRegister(itemUpgrade);

		for (int i = 0; i < initList.size(); i++) {
			initList.get(i).preInit();
		}
	}

	public static void initialize() {

		for (int i = 0; i < initList.size(); i++) {
			initList.get(i).initialize();
		}
	}

	public static void postInit() {

		for (int i = 0; i < initList.size(); i++) {
			initList.get(i).postInit();
		}
		initList.clear();
	}

	static ArrayList<IInitializer> initList = new ArrayList<IInitializer>();

	/* REFERENCES */
	public static ItemUpgrade itemUpgrade;

}