package cofh.thermalexpansion.util;

import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.crafting.*;
import cofh.thermalexpansion.util.fuels.CoolantManager;
import cofh.thermalexpansion.util.fuels.FuelManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

import java.util.List;
import java.util.Locale;

public class IMCHandler {

	public static IMCHandler instance = new IMCHandler();

	public void handleIMC(List<IMCMessage> messages) {

		NBTTagCompound theNBT;
		for (IMCMessage theMessage : messages) {
			try {
				if (theMessage.isNBTMessage()) {
					theNBT = theMessage.getNBTValue();

					if (theMessage.key.equalsIgnoreCase("FurnaceRecipe")) {
						FurnaceManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("output")));
						continue;
					} else if (theMessage.key.equalsIgnoreCase("RemoveFurnaceRecipe")) {
						FurnaceManager.removeRecipe(ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")));
						continue;
					} else if (theMessage.key.equalsIgnoreCase("PulverizerRecipe")) {
						if (theNBT.hasKey("secondaryChance")) {
							PulverizerManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryOutput")), theNBT.getInteger("secondaryChance"));
						} else if (theNBT.hasKey("secondaryOutput")) {
							PulverizerManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryOutput")));
						} else {
							PulverizerManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")));
						}
						continue;
					} else if (theMessage.key.equalsIgnoreCase("RemovePulverizerRecipe")) {
						PulverizerManager.removeRecipe(ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")));
						continue;
					} else if (theMessage.key.equalsIgnoreCase("SawmillRecipe")) {
						if (theNBT.hasKey("secondaryChance")) {
							SawmillManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryOutput")), theNBT.getInteger("secondaryChance"));
						} else if (theNBT.hasKey("secondaryOutput")) {
							SawmillManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryOutput")));
						} else {
							SawmillManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")));
						}
						continue;
					} else if (theMessage.key.equalsIgnoreCase("RemoveSawmillRecipe")) {
						SawmillManager.removeRecipe(ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")));
						continue;
					} else if (theMessage.key.equalsIgnoreCase("SmelterRecipe")) {
						if (theNBT.hasKey("secondaryChance")) {
							SmelterManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryInput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryInput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryOutput")), theNBT.getInteger("secondaryChance"));
						} else if (theNBT.hasKey("secondaryOutput")) {
							SmelterManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryInput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryInput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryOutput")));
						} else {
							SmelterManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryInput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryInput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")));
						}
						continue;
					} else if (theMessage.key.equalsIgnoreCase("RemoveSmelterRecipe")) {
						SmelterManager.removeRecipe(ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryInput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryInput")));
						continue;
					} else if (theMessage.key.equalsIgnoreCase("CrucibleRecipe")) {
						CrucibleManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")), FluidStack.loadFluidStackFromNBT(theNBT.getCompoundTag("output")));
						continue;
					} else if (theMessage.key.equalsIgnoreCase("RemoveCrucibleRecipe")) {
						CrucibleManager.removeRecipe(ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")));
						continue;
					} else if (theMessage.key.equalsIgnoreCase("TransposerFillRecipe")) {
						TransposerManager.addFillRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("output")), FluidStack.loadFluidStackFromNBT(theNBT.getCompoundTag("fluid")), theNBT.getBoolean("reversible"));
						continue;
					} else if (theMessage.key.equalsIgnoreCase("RemoveTransposerFillRecipe")) {
						TransposerManager.removeFillRecipe(ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")), FluidStack.loadFluidStackFromNBT(theNBT.getCompoundTag("fluid")));
						continue;
					} else if (theMessage.key.equalsIgnoreCase("TransposerExtractRecipe")) {
						TransposerManager.addExtractRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("output")), FluidStack.loadFluidStackFromNBT(theNBT.getCompoundTag("fluid")), theNBT.getInteger("chance"), theNBT.getBoolean("reversible"));
						continue;
					} else if (theMessage.key.equalsIgnoreCase("RemoveTransposerExtractRecipe")) {
						TransposerManager.removeExtractRecipe(ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")));
						continue;
					} else if (theMessage.key.equalsIgnoreCase("ChargerRecipe")) {
						ChargerManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("output")));
						continue;
					} else if (theMessage.key.equalsIgnoreCase("RemoveChargerRecipe")) {
						ChargerManager.removeRecipe(ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("input")));
						continue;
					} else if (theMessage.key.equalsIgnoreCase("InsolatorRecipe")) {
						if (theNBT.hasKey("secondaryChance")) {
							InsolatorManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryInput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryInput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryOutput")), theNBT.getInteger("secondaryChance"));
						} else if (theNBT.hasKey("secondaryOutput")) {
							InsolatorManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryInput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryInput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryOutput")));
						} else {
							InsolatorManager.addRecipe(theNBT.getInteger("energy"), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryInput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryInput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryOutput")));
						}
						continue;
					} else if (theMessage.key.equalsIgnoreCase("RemoveInsolatorRecipe")) {
						InsolatorManager.removeRecipe(ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("primaryInput")), ItemStack.loadItemStackFromNBT(theNBT.getCompoundTag("secondaryInput")));
						continue;
					} else if (theMessage.key.equalsIgnoreCase("MagmaticFuel")) {
						String fluidName = theNBT.getString("fluidName").toLowerCase(Locale.ENGLISH);
						int energy = theNBT.getInteger("energy");

						if (FuelManager.addMagmaticFuel(fluidName, energy)) {
							FuelManager.configFuels.get("Fuels.Magmatic", fluidName, energy);
						}
						continue;
					} else if (theMessage.key.equalsIgnoreCase("CompressionFuel")) {
						String fluidName = theNBT.getString("fluidName").toLowerCase(Locale.ENGLISH);
						int energy = theNBT.getInteger("energy");

						if (FuelManager.addCompressionFuel(fluidName, energy)) {
							FuelManager.configFuels.get("Fuels.Compression", fluidName, energy);
						}
						continue;
					} else if (theMessage.key.equalsIgnoreCase("ReactantFuel")) {
						String fluidName = theNBT.getString("fluidName").toLowerCase(Locale.ENGLISH);
						int energy = theNBT.getInteger("energy");

						if (FuelManager.addCompressionFuel(fluidName, energy)) {
							FuelManager.configFuels.get("Fuels.Reactant", fluidName, energy);
						}
						continue;
					} else if (theMessage.key.equalsIgnoreCase("Coolant")) {
						String fluidName = theNBT.getString("fluidName").toLowerCase(Locale.ENGLISH);
						int energy = theNBT.getInteger("energy");

						if (CoolantManager.addCoolant(fluidName, energy)) {
							FuelManager.configFuels.get("Coolants", fluidName, energy);
						}
						continue;
					}
					ThermalExpansion.LOG.warn("Thermal Expansion received an invalid IMC from " + theMessage.getSender() + "! Key was " + theMessage.key);
				}
			} catch (Exception e) {
				ThermalExpansion.LOG.warn("Thermal Expansion received a broken IMC from " + theMessage.getSender() + "!");
				e.printStackTrace();
			}
		}
	}

}
