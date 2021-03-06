package cofh.thermalexpansion.block.machine;

import cofh.api.core.ICustomInventory;
import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.AugmentHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.machine.GuiExtruder;
import cofh.thermalexpansion.gui.container.machine.ContainerExtruder;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class TileExtruder extends TileMachineBase implements ICustomInventory {

	private static final int TYPE = BlockMachine.Type.EXTRUDER.getMetadata();
	public static int basePower = 20;

	public static ItemStack ANDESITE;
	public static ItemStack DIORITE;
	public static ItemStack GRANITE;

	public static void initialize() {

		processItems = new ItemStack[3];

		processItems[0] = new ItemStack(Blocks.COBBLESTONE);
		processItems[1] = new ItemStack(Blocks.STONE);
		processItems[2] = new ItemStack(Blocks.OBSIDIAN);

		GRANITE = new ItemStack(Blocks.STONE, 1, 1);
		DIORITE = new ItemStack(Blocks.STONE, 1, 3);
		ANDESITE = new ItemStack(Blocks.STONE, 1, 5);

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 4;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, {}, { 0 }, { 0 } };
		SIDE_CONFIGS[TYPE].allowInsertionSide = new boolean[] { false, true, false, true };
		SIDE_CONFIGS[TYPE].allowExtractionSide = new boolean[] { false, false, true, true };
		SIDE_CONFIGS[TYPE].sideTex = new int[] { 0, 1, 4, 7 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { true, false };

		VALID_AUGMENTS[TYPE] = new ArrayList<>();
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_EXTRUDER_ANDESITE);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_EXTRUDER_DIORITE);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_EXTRUDER_GRANITE);

		LIGHT_VALUES[TYPE] = 14;

		GameRegistry.registerTileEntity(TileExtruder.class, "thermalexpansion:machine_extruder");

		config();
	}

	public static void config() {

		String category = "Machine.Extruder";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower);

	}

	private static int[] processLava = { 0, 0, 1000 };
	private static int[] processWater = { 0, 1000, 1000 };
	private static int[] processEnergy = { 800, 800, 1600 };
	private static ItemStack[] processItems = new ItemStack[3];

	private int outputTracker;
	private byte curSelection;
	private byte prevSelection;

	private ItemStack[] outputItems = new ItemStack[3];
	private FluidTankCore hotTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidTankCore coldTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);

	/* AUGMENTS */
	protected boolean augmentAndesite;
	protected boolean augmentDiorite;
	protected boolean augmentGranite;

	public TileExtruder() {

		super();
		inventory = new ItemStack[1 + 1];

		for (int i = 0; i < 3; i++) {
			outputItems[i] = processItems[i].copy();
		}
		hotTank.setLock(FluidRegistry.LAVA);
		coldTank.setLock(FluidRegistry.WATER);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected boolean canStart() {

		if (hotTank.getFluidAmount() < Fluid.BUCKET_VOLUME || coldTank.getFluidAmount() < Fluid.BUCKET_VOLUME || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		if (inventory[0] == null) {
			return true;
		}
		if (!inventory[0].isItemEqual(outputItems[curSelection])) {
			return false;
		}
		return inventory[0].stackSize + outputItems[curSelection].stackSize <= outputItems[prevSelection].getMaxStackSize();
	}

	@Override
	protected boolean canFinish() {

		return processRem <= 0;
	}

	@Override
	protected void processStart() {

		processMax = processEnergy[curSelection] * energyMod / ENERGY_BASE;
		processRem = processMax;
		prevSelection = curSelection;
	}

	@Override
	protected void processFinish() {

		if (inventory[0] == null) {
			inventory[0] = outputItems[prevSelection].copy();
		} else {
			inventory[0].stackSize += outputItems[prevSelection].stackSize;
		}
		hotTank.drain(processLava[prevSelection], true);
		coldTank.drain(processWater[prevSelection], true);
		prevSelection = curSelection;
	}

	@Override
	protected void transferOutput() {

		if (!enableAutoOutput) {
			return;
		}
		if (inventory[0] == null) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (sideCache[side] == 2) {
				if (transferItem(0, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	@Override
	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		if (!super.readPortableTagInternal(player, tag)) {
			return false;
		}
		if (tag.hasKey("Sel")) {
			curSelection = tag.getByte("Sel");
			if (!isActive) {
				prevSelection = curSelection;
			}
		}
		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		if (!super.writePortableTagInternal(player, tag)) {
			return false;
		}
		tag.setByte("Sel", curSelection);
		return true;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiExtruder(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerExtruder(inventory, this);
	}

	public int getCurSelection() {

		return curSelection;
	}

	public int getPrevSelection() {

		return prevSelection;
	}

	public FluidTankCore getTank(int tankIndex) {

		if (tankIndex == 0) {
			return hotTank;
		}
		return coldTank;
	}

	public FluidStack getTankFluid(int tankIndex) {

		if (tankIndex == 0) {
			return hotTank.getFluid();
		}
		return coldTank.getFluid();
	}

	public void setMode(int selection) {

		byte lastSelection = curSelection;
		curSelection = (byte) selection;
		sendModePacket();
		curSelection = lastSelection;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		outputTracker = nbt.getInteger("TrackOut");
		prevSelection = nbt.getByte("Prev");
		curSelection = nbt.getByte("Sel");

		hotTank.readFromNBT(nbt.getCompoundTag("HotTank"));
		coldTank.readFromNBT(nbt.getCompoundTag("ColdTank"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackOut", outputTracker);
		nbt.setByte("Prev", prevSelection);
		nbt.setByte("Sel", curSelection);

		nbt.setTag("HotTank", hotTank.writeToNBT(new NBTTagCompound()));
		nbt.setTag("ColdTank", coldTank.writeToNBT(new NBTTagCompound()));
		return nbt;
	}

	/* NETWORK METHODS */

	/* CLIENT -> SERVER */
	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addByte(curSelection);

		return payload;
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		curSelection = payload.getByte();

		if (!isActive) {
			prevSelection = curSelection;
		}
		callNeighborTileChange();
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addByte(curSelection);
		payload.addByte(prevSelection);
		payload.addInt(hotTank.getFluidAmount());
		payload.addInt(coldTank.getFluidAmount());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		curSelection = payload.getByte();
		prevSelection = payload.getByte();
		hotTank.getFluid().amount = payload.getInt();
		coldTank.getFluid().amount = payload.getInt();
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		outputItems[1] = processItems[1].copy();

		augmentGranite = false;
		augmentDiorite = false;
		augmentAndesite = false;
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentGranite && TEProps.MACHINE_EXTRUDER_GRANITE.equals(id)) {
			outputItems[1] = GRANITE.copy();
			augmentGranite = true;
			hasModeAugment = true;
			return true;
		}
		if (!augmentDiorite && TEProps.MACHINE_EXTRUDER_DIORITE.equals(id)) {
			outputItems[1] = DIORITE.copy();
			augmentDiorite = true;
			hasModeAugment = true;
			return true;
		}
		if (!augmentAndesite && TEProps.MACHINE_EXTRUDER_ANDESITE.equals(id)) {
			outputItems[1] = ANDESITE.copy();
			augmentAndesite = true;
			hasModeAugment = true;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* ICustomInventory */
	@Override
	public ItemStack[] getInventorySlots(int inventoryIndex) {

		return outputItems;
	}

	@Override
	public int getSlotStackLimit(int slotIndex) {

		return 64;
	}

	@Override
	public void onSlotUpdate() {

		markDirty();
	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return super.hasCapability(capability, from) || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {
				@Override
				public IFluidTankProperties[] getTankProperties() {

					FluidTankInfo hotInfo = hotTank.getInfo();
					FluidTankInfo coldInfo = coldTank.getInfo();
					return new IFluidTankProperties[] { new FluidTankProperties(hotInfo.fluid, hotInfo.capacity, true, false), new FluidTankProperties(coldInfo.fluid, coldInfo.capacity, true, false) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (from != null && sideCache[from.ordinal()] != 1) {
						return 0;
					}
					if (resource.getFluid() == FluidRegistry.LAVA) {
						return hotTank.fill(resource, doFill);
					} else if (resource.getFluid() == FluidRegistry.WATER) {
						return coldTank.fill(resource, doFill);
					}
					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					return null;
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
