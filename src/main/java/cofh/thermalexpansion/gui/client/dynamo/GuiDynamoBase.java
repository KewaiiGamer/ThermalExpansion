package cofh.thermalexpansion.gui.client.dynamo;

import cofh.lib.gui.container.IAugmentableContainer;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.gui.element.tab.TabAugment;
import cofh.lib.gui.element.tab.TabBase;
import cofh.lib.gui.element.tab.TabEnergy;
import cofh.lib.gui.element.tab.TabInfo;
import cofh.lib.gui.element.tab.TabRedstone;
import cofh.lib.gui.element.tab.TabSecurity;
import cofh.lib.gui.element.tab.TabTutorial;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.dynamo.TileDynamoBase;
import cofh.thermalexpansion.gui.client.GuiTEBase;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public abstract class GuiDynamoBase extends GuiTEBase {

	protected TileDynamoBase myTile;
	protected UUID playerName;

	public String myInfo = "";
	public String myTutorial = StringHelper.tutorialTabAugment();

	protected TabBase redstoneTab;

	public GuiDynamoBase(Container container, TileEntity tile, EntityPlayer player, ResourceLocation texture) {

		super(container, texture);

		myTile = (TileDynamoBase) tile;
		name = myTile.getDisplayName().getUnformattedText();
		playerName = SecurityHelper.getID(player);

		if (myTile.augmentRedstoneControl) {
			myTutorial += "\n\n" + StringHelper.tutorialTabRedstone();
		}
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 80, 18, myTile.getEnergyStorage()));

		addTab(new TabAugment(this, (IAugmentableContainer) inventorySlots));
		if (myTile.enableSecurity() && myTile.isSecured()) {
			addTab(new TabSecurity(this, myTile, playerName));
		}
		redstoneTab = addTab(new TabRedstone(this, myTile));

		if (myTile.getMaxEnergyStored(EnumFacing.DOWN) > 0) {
			addTab(new TabEnergy(this, myTile, true));
		}
		addTab(new TabInfo(this, myInfo + "\n\n" + StringHelper.localize("tab.thermalexpansion.dynamo.0")));
		addTab(new TabTutorial(this, myTutorial));
	}

	@Override
	public void updateScreen() {

		super.updateScreen();

		if (!myTile.canAccess()) {
			this.mc.thePlayer.closeScreen();
		}
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		redstoneTab.setVisible(myTile.augmentRedstoneControl);
	}

}
