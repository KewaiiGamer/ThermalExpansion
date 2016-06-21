package cofh.thermalexpansion.gui.client;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.GuiProps;
import cofh.lib.util.helpers.StringHelper;

import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public abstract class GuiTEBase extends GuiBase {

	public static final String TEX_ARROW_LEFT = GuiProps.PATH_ELEMENTS + "progress_arrow_left.png";
	public static final String TEX_ARROW_RIGHT = GuiProps.PATH_ELEMENTS + "progress_arrow_right.png";
	public static final String TEX_DROP_LEFT = GuiProps.PATH_ELEMENTS + "progress_fluid_left.png";
	public static final String TEX_DROP_RIGHT = GuiProps.PATH_ELEMENTS + "progress_fluid_right.png";

	public static final String TEX_ALCHEMY = GuiProps.PATH_ELEMENTS + "scale_alchemy.png";
	public static final String TEX_BUBBLE = GuiProps.PATH_ELEMENTS + "scale_bubble.png";
	public static final String TEX_CRUSH = GuiProps.PATH_ELEMENTS + "scale_crush.png";
	public static final String TEX_FLAME = GuiProps.PATH_ELEMENTS + "scale_flame.png";
	public static final String TEX_FLUX = GuiProps.PATH_ELEMENTS + "scale_flux.png";
	public static final String TEX_SAW = GuiProps.PATH_ELEMENTS + "scale_saw.png";
	public static final String TEX_SPIN = GuiProps.PATH_ELEMENTS + "scale_spin.png";
	public static final String TEX_SUN = GuiProps.PATH_ELEMENTS + "scale_sun.png";
	public static final String TEX_SNOWFLAKE = GuiProps.PATH_ELEMENTS + "scale_snowflake.png";

	public static final String TEX_INFO_ANGLE = GuiProps.PATH_ELEMENTS + "info_angle.png";
	public static final String TEX_INFO_DISTANCE = GuiProps.PATH_ELEMENTS + "info_distance.png";
	public static final String TEX_INFO_DURATION = GuiProps.PATH_ELEMENTS + "info_duration.png";
	public static final String TEX_INFO_FORCE = GuiProps.PATH_ELEMENTS + "info_force.png";
	public static final String TEX_INFO_SIGNAL = GuiProps.PATH_ELEMENTS + "info_signal.png";

	public static final String TEX_TANK = GuiProps.PATH_ELEMENTS + "fluid_tank.png";
	public static final String TEX_TANK_GREY = GuiProps.PATH_ELEMENTS + "fluid_tank_grey.png";

	public static final int PROGRESS = 24;
	public static final int SPEED = 16;

	protected String myInfo = "";

	public GuiTEBase(Container container) {

		super(container);
	}

	public GuiTEBase(Container container, ResourceLocation texture) {

		super(container, texture);
	}

	protected void generateInfo(String tileString, int lines) {

		myInfo = StringHelper.localize(tileString + "." + 0);
		for (int i = 1; i < lines; i++) {
			myInfo += "\n\n" + StringHelper.localize(tileString + "." + i);
		}
	}

}
