package cofh.thermalexpansion.block.machine;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.blockbakery.*;
import codechicken.lib.texture.IWorldBlockTextureProvider;
import codechicken.lib.texture.TextureUtils;
import cofh.api.core.IModelRegister;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.item.ItemFrame;
import cofh.thermalexpansion.render.RenderMachine;
import cofh.thermalexpansion.util.ReconfigurableHelper;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;
import static cofh.lib.util.helpers.ItemHelper.addRecipe;

public class BlockMachine extends BlockTEBase implements IModelRegister, IBakeryBlock, IWorldBlockTextureProvider {

	public static final PropertyEnum<BlockMachine.Type> VARIANT = PropertyEnum.create("type", Type.class);

	public BlockMachine() {

		super(Material.IRON);

		setUnlocalizedName("machine");

		setHardness(15.0F);
		setResistance(25.0F);
		setDefaultState(getBlockState().getBaseState().withProperty(VARIANT, Type.FURNACE));
	}

	@Override
	protected BlockStateContainer createBlockState() {

		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
		// Listed
		builder.add(VARIANT);
		// UnListed
		builder.add(TEProps.LEVEL);
		builder.add(TEProps.ACTIVE);
		builder.add(TEProps.FACING);
		builder.add(TEProps.SIDE_CONFIG);
		builder.add(TEProps.TILE);

		return builder.build();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {

		for (int i = 0; i < Type.METADATA_LOOKUP.length; i++) {
			if (enable[i]) {
				//				for (int j = 0; j < 5; j++) {
				//					list.add(ItemBlockMachine.setDefaultTag(new ItemStack(item, 1, i), j));
				//				}
				list.add(itemBlock.setDefaultTag(new ItemStack(item, 1, i), 4));
			}
		}
	}

	/* TYPE METHODS */
	@Override
	public IBlockState getStateFromMeta(int meta) {

		return this.getDefaultState().withProperty(VARIANT, Type.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {

		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	public int damageDropped(IBlockState state) {

		return state.getValue(VARIANT).getMetadata();
	}

	/* ITileEntityProvider */
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= Type.values().length) {
			return null;
		}
		switch (Type.byMetadata(metadata)) {
			case FURNACE:
				return new TileFurnace();
			case PULVERIZER:
				return new TilePulverizer();
			case SAWMILL:
				return new TileSawmill();
			case SMELTER:
				return new TileSmelter();
			case INSOLATOR:
				return new TileInsolator();
			case COMPACTOR:
				return new TileCompactor();
			case CRUCIBLE:
				return new TileCrucible();
			case REFINERY:
				return new TileRefinery();
			case TRANSPOSER:
				return new TileTransposer();
			case CHARGER:
				return new TileCharger();
			case CENTRIFUGE:                    // TODO
				return null;
			case CRAFTER:                       // TODO
				return null;
			case BREWER:                        // TODO
				return null;
			case ENCHANTER:                     // TODO
				return null;
			case PRECIPITATOR:
				return new TilePrecipitator();
			case EXTRUDER:
				return new TileExtruder();
			default:
				return null;
		}
	}

	/* BLOCK METHODS */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {

		if (stack.getTagCompound() != null) {
			TileMachineBase tile = (TileMachineBase) world.getTileEntity(pos);

			tile.setLevel(stack.getTagCompound().getByte("Level"));
			tile.readAugmentsFromNBT(stack.getTagCompound());
			tile.updateAugmentStatus();
			tile.setEnergyStored(stack.getTagCompound().getInteger("Energy"));

			int facing = BlockHelper.determineXZPlaceFacing(living);
			int storedFacing = ReconfigurableHelper.getFacing(stack);
			byte[] sideCache = ReconfigurableHelper.getSideCache(stack, tile.getDefaultSides());

			tile.sideCache[0] = sideCache[0];
			tile.sideCache[1] = sideCache[1];
			tile.sideCache[facing] = 0;
			tile.sideCache[BlockHelper.getLeftSide(facing)] = sideCache[BlockHelper.getLeftSide(storedFacing)];
			tile.sideCache[BlockHelper.getRightSide(facing)] = sideCache[BlockHelper.getRightSide(storedFacing)];
			tile.sideCache[BlockHelper.getOppositeSide(facing)] = sideCache[BlockHelper.getOppositeSide(storedFacing)];
		}
		super.onBlockPlacedBy(world, pos, state, living, stack);
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {

		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {

		return true;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		return true;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

		TileEntity tile = world.getTileEntity(pos);

		if (!(tile instanceof TileTransposer) && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
			IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
			if (FluidHelper.drainItemToHandler(heldItem, handler, player, hand)) {
				return true;
			}
		}
		return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
	}

	/* RENDERING METHODS */
	@Override
	@SideOnly (Side.CLIENT)
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {

		return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

		return BlockBakery.handleExtendedState((IExtendedBlockState) super.getExtendedState(state, world, pos), world.getTileEntity(pos));
	}

	@Override
	public ICustomBlockBakery getCustomBakery() {
		return RenderMachine.INSTANCE;
	}

	@Override // Inventory
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(EnumFacing side, ItemStack stack) {

		// boolean isCreative = ItemBlockMachine.isCreative(stack);
		int level = itemBlock.getLevel(stack);

		if (side == EnumFacing.DOWN) {
			return TETextures.MACHINE_BOTTOM[level];
		}
		if (side == EnumFacing.UP) {
			return TETextures.MACHINE_TOP[level];
		}
		return side != EnumFacing.NORTH ? TETextures.MACHINE_SIDE[level] : TETextures.MACHINE_FACE[stack.getMetadata() % Type.values().length];
	}

	@Override // World
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(EnumFacing side, IBlockState state, BlockRenderLayer layer, IBlockAccess world, BlockPos pos) {

		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileMachineBase) {
			TileMachineBase tile = ((TileMachineBase) tileEntity);
			return tile.getTexture(side.ordinal(), layer == BlockRenderLayer.SOLID ? 0 : 1);
		}
		return TextureUtils.getMissingSprite();
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		StateMap.Builder stateMap = new StateMap.Builder();
		stateMap.ignore(VARIANT);
		ModelLoader.setCustomStateMapper(this, stateMap.build());

		ModelResourceLocation location = new ModelResourceLocation(getRegistryName(), "normal");
		for (Type type : Type.values()) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMetadata(), location);
		}
		ModelRegistryHelper.register(location, new CCBakeryModel("thermalexpansion:blocks/machine/machine_top_0"));

		BlockBakery.registerItemKeyGenerator(itemBlock, stack -> BlockBakery.defaultItemKeyGenerator.generateKey(stack) + ",level=" + itemBlock.getLevel(stack));
		BlockBakery.registerBlockKeyGenerator(this, state -> {
			StringBuilder builder = new StringBuilder(state.getBlock().getRegistryName() + "|" + state.getBlock().getMetaFromState(state));
			builder.append(",level=").append(state.getValue(TEProps.LEVEL));
			builder.append(",facing=").append(state.getValue(TEProps.FACING));
			builder.append(",active=").append(state.getValue(TEProps.ACTIVE));
			builder.append(",side_config={");
			for (int i : state.getValue(TEProps.SIDE_CONFIG)) {
				builder.append(",").append(i);
			}
			builder.append("}");
			return builder.toString();
		});
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		this.setRegistryName("machine");
		GameRegistry.register(this);

		itemBlock = new ItemBlockMachine(this);
		itemBlock.setRegistryName(this.getRegistryName());
		GameRegistry.register(itemBlock);

		return true;
	}

	@Override
	public boolean initialize() {

		TileMachineBase.config();

		TileFurnace.initialize();
		TilePulverizer.initialize();
		TileSawmill.initialize();
		TileSmelter.initialize();
		TileInsolator.initialize();
		TileCompactor.initialize();
		TileCrucible.initialize();
		TileRefinery.initialize();
		TileTransposer.initialize();
		TileCharger.initialize();
		// centrifuge
		// crafter
		// brewer
		// enchanter
		TilePrecipitator.initialize();
		TileExtruder.initialize();

		machineFurnace = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.FURNACE.getMetadata()));
		machinePulverizer = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.PULVERIZER.getMetadata()));
		machineSawmill = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.SAWMILL.getMetadata()));
		machineSmelter = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.SMELTER.getMetadata()));
		machineInsolator = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.INSOLATOR.getMetadata()));
		machineCompactor = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.COMPACTOR.getMetadata()));
		machineCrucible = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.CRUCIBLE.getMetadata()));
		machineRefinery = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.REFINERY.getMetadata()));
		machineTransposer = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.TRANSPOSER.getMetadata()));
		machineCharger = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.CHARGER.getMetadata()));
		// centrifuge
		// machineCrafter = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.CRAFTER.getMetadata()));
		// brewer
		// enchanter
		machinePrecipitator = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.PRECIPITATOR.getMetadata()));
		machineExtruder = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.EXTRUDER.getMetadata()));

		return true;
	}

	@Override
	public boolean postInit() {

		String copperPart = "gearCopper";
		String invarPart = "gearInvar";

		// @formatter:off
		if (enable[Type.FURNACE.getMetadata()]) {
			addRecipe(ShapedRecipe(machineFurnace,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', "dustRedstone",
					'Y', Blocks.BRICK_BLOCK
			));
		}
		if (enable[Type.PULVERIZER.getMetadata()]) {
			addRecipe(ShapedRecipe(machinePulverizer,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', Blocks.PISTON,
					'Y', Items.FLINT
			));
		}
		if (enable[Type.SAWMILL.getMetadata()]) {
			addRecipe(ShapedRecipe(machineSawmill,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', "gearIron",
					'Y', "plankWood"
			));
		}
		if (enable[Type.SMELTER.getMetadata()]) {
			addRecipe(ShapedRecipe(machineSmelter,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', invarPart,
					'P', ItemMaterial.powerCoilGold,
					'X', Items.BUCKET,
					'Y', "ingotInvar"
			));
		}
		if (enable[Type.INSOLATOR.getMetadata()]) {
			addRecipe(ShapedRecipe(machineInsolator,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', "gearLumium",
					'Y', "dirt"
			));
		}
		if (enable[Type.COMPACTOR.getMetadata()]) {
			addRecipe(ShapedRecipe(machineCompactor,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', Blocks.PISTON,
					'Y', "ingotBronze"
			));
		}
		if (enable[Type.CRUCIBLE.getMetadata()]) {
			addRecipe(ShapedRecipe(machineCrucible,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', invarPart,
					'P', ItemMaterial.powerCoilGold,
					'X', ItemMaterial.powerCoilElectrum,
					'Y', Blocks.NETHER_BRICK
			));
		}
		if (enable[Type.REFINERY.getMetadata()]) {
			addRecipe(ShapedRecipe(machineRefinery,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', "gearNickel",
					'Y', "blockGlass"
			));
		}
		if (enable[Type.TRANSPOSER.getMetadata()]) {
			addRecipe(ShapedRecipe(machineTransposer,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', invarPart,
					'P', ItemMaterial.powerCoilGold,
					'X', Items.BUCKET,
					'Y', "blockGlass"
			));
		}
		if (enable[Type.CHARGER.getMetadata()]) {
			addRecipe(ShapedRecipe(machineCharger,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', ItemMaterial.powerCoilElectrum,
					'Y', ItemMaterial.powerCoilSilver
			));
		}
//		if (enable[Type.CRAFTER.getMetadata()]) {
//			addRecipe(ShapedRecipe(machineCrafter,
//					" X ",
//					"YCY",
//					"IPI",
//					'C', ItemFrame.frameMachine,
//					'I', copperPart,
//					'P', ItemMaterial.powerCoilGold,
//					'X', "chestWood",
//					'Y', "gearTin"
//			));
//		}
		if (enable[Type.PRECIPITATOR.getMetadata()]) {
			addRecipe(ShapedRecipe(machinePrecipitator,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', Blocks.PISTON,
					'Y', "ingotInvar"
			));
		}
		if (enable[Type.EXTRUDER.getMetadata()]) {
			addRecipe(ShapedRecipe(machineExtruder,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', invarPart,
					'P', ItemMaterial.powerCoilGold,
					'X', Blocks.PISTON,
					'Y', "blockGlass"
			));
		}
		// @formatter:on

		return true;
	}

	/* TYPE */
	public enum Type implements IStringSerializable {

		// @formatter:off
		FURNACE(0, "furnace"),
		PULVERIZER(1, "pulverizer"),
		SAWMILL(2, "sawmill"),
		SMELTER(3, "smelter"),
		INSOLATOR(4, "insolator"),
		COMPACTOR(5, "compactor"),
		CRUCIBLE(6, "crucible"),
		REFINERY(7, "refinery"),
		TRANSPOSER(8, "transposer"),
		CHARGER(9, "charger"),
		CENTRIFUGE(10, "centrifuge"),
		CRAFTER(11, "crafter"),
		BREWER(12, "brewer"),
		ENCHANTER(13, "enchanter"),
		PRECIPITATOR(14, "precipitator"),
		EXTRUDER(15, "extruder");
		// @formatter:on

		private static final BlockMachine.Type[] METADATA_LOOKUP = new BlockMachine.Type[values().length];
		private final int metadata;
		private final String name;
		private final int light;

		Type(int metadata, String name, int light) {

			this.metadata = metadata;
			this.name = name;
			this.light = light;
		}

		Type(int metadata, String name) {

			this(metadata, name, 0);
		}

		public int getMetadata() {

			return this.metadata;
		}

		@Override
		public String getName() {

			return this.name;
		}

		public int getLight() {

			return this.light;
		}

		public static Type byMetadata(int metadata) {

			if (metadata < 0 || metadata >= METADATA_LOOKUP.length) {
				metadata = 0;
			}
			return METADATA_LOOKUP[metadata];
		}

		static {
			for (Type type : values()) {
				METADATA_LOOKUP[type.getMetadata()] = type;
			}
		}
	}

	public static boolean[] enable = new boolean[Type.values().length];

	/* REFERENCES */
	public static ItemStack machineFurnace;
	public static ItemStack machinePulverizer;
	public static ItemStack machineSawmill;
	public static ItemStack machineSmelter;
	public static ItemStack machineInsolator;
	public static ItemStack machineCompactor;
	public static ItemStack machineCrucible;
	public static ItemStack machineRefinery;
	public static ItemStack machineTransposer;
	public static ItemStack machineCharger;
	public static ItemStack machineCentrifuge;
	public static ItemStack machineCrafter;
	public static ItemStack machineBrewer;
	public static ItemStack machineEnchanter;
	public static ItemStack machinePrecipitator;
	public static ItemStack machineExtruder;

	public static ItemBlockMachine itemBlock;

}
