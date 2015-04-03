package cofh.thermalexpansion.block.device;

import cofh.api.inventory.IInventoryConnection;
import cofh.core.CoFHProps;
import cofh.core.entity.CoFHFakePlayer;
import cofh.core.render.IconRegistry;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.InventoryHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileAugmentable;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.client.device.GuiBreaker;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileBreaker extends TileAugmentable implements IFluidHandler, IInventoryConnection {

	static final int TYPE = BlockDevice.Types.BREAKER.ordinal();
	static SideConfig defaultSideConfig = new SideConfig();

	public static void initialize() {

		defaultSideConfig = new SideConfig();
		defaultSideConfig.numGroup = 2;
		defaultSideConfig.slotGroups = new int[][] { {}, {} };
		defaultSideConfig.allowInsertion = new boolean[] { false, false };
		defaultSideConfig.allowExtraction = new boolean[] { false, false };
		defaultSideConfig.sideTex = new int[] { 0, 4 };
		defaultSideConfig.defaultSides = new byte[] { 0, 0, 0, 0, 0, 0 };

		GameRegistry.registerTileEntity(TileBreaker.class, "thermalexpansion.Breaker");
		configure();
	}

	public static void configure() {

		String comment = "Enable this to allow for Breakers to be securable.";
		enableSecurity = ThermalExpansion.config.get("Security", "Device.Breaker.Securable", enableSecurity, comment);
	}

	public static boolean enableSecurity = true;

	boolean needsWorld = true;
	CoFHFakePlayer myFakePlayer;

	public LinkedList<ItemStack> stuffedItems = new LinkedList<ItemStack>();

	public TileBreaker() {

		sideConfig = defaultSideConfig;
	}

	@Override
	public void setDefaultSides() {

		sideCache = getDefaultSides();
		sideCache[facing ^ 1] = 1;
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
	}

	@Override
	public boolean sendRedstoneUpdates() {

		return true;
	}

	@Override
	public void updateEntity() {

		super.updateEntity();
		if (ServerHelper.isServerWorld(worldObj) && worldObj.getTotalWorldTime() % CoFHProps.TIME_CONSTANT_HALF == 0 && redstoneControlOrDisable()) {
			if (!isEmpty()) {
				outputBuffer();
			}
			if (isEmpty()) {
				updateFakePlayer();
				breakBlock();
			}
		}
	}

	public boolean isEmpty() {

		return stuffedItems.size() == 0;
	}

	public void breakBlock() {

		int coords[] = BlockHelper.getAdjacentCoordinatesForSide(xCoord, yCoord, zCoord, facing);
		Block block = worldObj.getBlock(coords[0], coords[1], coords[2]);
		FluidStack theStack = FluidHelper.getFluidFromWorld(worldObj, coords[0], coords[1], coords[2], true);
		if (theStack != null) {
			for (int i = 0; i < 6 && theStack.amount > 0; i++) {
				if (sideCache[i] == 1) {
					theStack.amount -= FluidHelper.insertFluidIntoAdjacentFluidHandler(this, i, theStack, true);
				}
			}
			worldObj.setBlockToAir(coords[0], coords[1], coords[2]);
		} else if (CoFHFakePlayer.isBlockBreakable(myFakePlayer, worldObj, coords[0], coords[1], coords[2])) {
			stuffedItems.addAll(BlockHelper.breakBlock(worldObj, coords[0], coords[1], coords[2], block, 0, true, false));
		}
	}

	public void outputBuffer() {

		for (int i = 0; i < 6; i++) {
			if (i != facing && sideCache[i] == 1) {
				int coords[] = BlockHelper.getAdjacentCoordinatesForSide(xCoord, yCoord, zCoord, i);
				TileEntity theTile = worldObj.getTileEntity(coords[0], coords[1], coords[2]);

				if (InventoryHelper.isInsertion(theTile)) {
					LinkedList<ItemStack> newStuffed = new LinkedList<ItemStack>();
					for (ItemStack curItem : stuffedItems) {
						if (curItem == null || curItem.getItem() == null) {
							curItem = null;
						} else {
							curItem = InventoryHelper.addToInsertion(theTile, i, curItem);
						}
						if (curItem != null) {
							newStuffed.add(curItem);
						}
					}
					stuffedItems = newStuffed;
				}
			}
		}
	}

	public void updateFakePlayer() {

		if (needsWorld) {
			myFakePlayer = new CoFHFakePlayer((WorldServer) worldObj);
			needsWorld = false;
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiBreaker(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		NBTTagList list = nbt.getTagList("StuffedInv", 10);
		stuffedItems.clear();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			stuffedItems.add(ItemStack.loadItemStackFromNBT(compound));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		NBTTagList list = new NBTTagList();
		list = new NBTTagList();
		for (int i = 0; i < stuffedItems.size(); i++) {
			if (stuffedItems.get(i) != null) {
				NBTTagCompound compound = new NBTTagCompound();
				stuffedItems.get(i).writeToNBT(compound);
				list.appendTag(compound);
			}
		}
		nbt.setTag("StuffedInv", list);
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return CoFHProps.EMPTY_TANK_INFO;
	}

	/* IInventoryConnection */
	@Override
	public ConnectionType canConnectInventory(ForgeDirection from) {

		if (from != ForgeDirection.UNKNOWN && from.ordinal() != facing && sideCache[from.ordinal()] == 1) {
			return ConnectionType.FORCE;
		} else {
			return ConnectionType.DEFAULT;
		}
	}

	/* IReconfigurableFacing */
	@Override
	public boolean allowYAxisFacing() {

		return true;
	}

	@Override
	public boolean setFacing(int side) {

		if (side < 0 || side > 5) {
			return false;
		}
		facing = (byte) side;
		sideCache[facing] = 0;
		sideCache[facing ^ 1] = 1;
		markDirty();
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	/* ISidedTexture */
	@Override
	public IIcon getTexture(int side, int pass) {

		if (pass == 0) {
			return side != facing ? IconRegistry.getIcon("DeviceSide") : redstoneControlOrDisable() ? IconRegistry.getIcon("DeviceActive", getType())
					: IconRegistry.getIcon("DeviceFace", getType());
		} else if (side < 6) {
			return IconRegistry.getIcon(TEProps.textureSelection, sideConfig.sideTex[sideCache[side]]);
		}
		return IconRegistry.getIcon("DeviceSide");
	}

}
