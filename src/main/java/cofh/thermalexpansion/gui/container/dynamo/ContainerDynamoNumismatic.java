package cofh.thermalexpansion.gui.container.dynamo;

import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.dynamo.TileDynamoNumismatic;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerDynamoNumismatic extends ContainerTEBase implements ISlotValidator {

	TileDynamoNumismatic myTile;

	public ContainerDynamoNumismatic(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileDynamoNumismatic) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 44, 35));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return TileDynamoNumismatic.getEnergyValue(stack) > 0;
	}

}
