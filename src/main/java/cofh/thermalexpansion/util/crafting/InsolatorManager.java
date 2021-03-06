package cofh.thermalexpansion.util.crafting;

import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalfoundation.item.ItemFertilizer;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;
import java.util.Map.Entry;

public class InsolatorManager {

	private static Map<List<ComparableItemStackInsolator>, RecipeInsolator> recipeMap = new THashMap<>();
	private static Set<ComparableItemStackInsolator> validationSet = new THashSet<>();
	private static Set<ComparableItemStackInsolator> lockSet = new THashSet<>();

	static final int CROP_MULTIPLIER_RICH = 3;
	static final int CROP_MULTIPLIER_FLUX = 5;
	static final int DEFAULT_ENERGY = 5000;
	static final int DEFAULT_ENERGY_RICH = 7500;
	static final int DEFAULT_ENERGY_FLUX = 10000;

	public static boolean isRecipeReversed(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput == null || secondaryInput == null) {
			return false;
		}
		ComparableItemStackInsolator query = new ComparableItemStackInsolator(primaryInput);
		ComparableItemStackInsolator querySecondary = new ComparableItemStackInsolator(secondaryInput);

		RecipeInsolator recipe = recipeMap.get(Arrays.asList(query, querySecondary));
		return recipe == null && recipeMap.get(Arrays.asList(querySecondary, query)) != null;
	}

	public static RecipeInsolator getRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput == null || secondaryInput == null) {
			return null;
		}
		ComparableItemStackInsolator query = new ComparableItemStackInsolator(primaryInput);
		ComparableItemStackInsolator querySecondary = new ComparableItemStackInsolator(secondaryInput);

		RecipeInsolator recipe = recipeMap.get(Arrays.asList(query, querySecondary));

		if (recipe == null) {
			recipe = recipeMap.get(Arrays.asList(querySecondary, query));
		}
		if (recipe == null) {
			return null;
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack primaryInput, ItemStack secondaryInput) {

		return getRecipe(primaryInput, secondaryInput) != null;
	}

	public static RecipeInsolator[] getRecipeList() {

		return recipeMap.values().toArray(new RecipeInsolator[recipeMap.values().size()]);
	}

	public static boolean isItemValid(ItemStack input) {

		return input != null && validationSet.contains(new ComparableItemStackInsolator(input));
	}

	public static boolean isItemFertilizer(ItemStack input) {

		return input != null && lockSet.contains(new ComparableItemStackInsolator(input));
	}

	public static void addDefaultRecipes() {

		/* FERTILIZER */
		{
			addFertilizer(ItemFertilizer.fertilizerBasic);
			addFertilizer(ItemFertilizer.fertilizerRich);
			addFertilizer(ItemFertilizer.fertilizerFlux);
		}

		/* CROPS */
		{
			addDefaultRecipe(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.WHEAT), new ItemStack(Items.WHEAT_SEEDS), 150);
			addDefaultRecipe(new ItemStack(Items.BEETROOT_SEEDS), new ItemStack(Items.BEETROOT), new ItemStack(Items.BEETROOT_SEEDS), 150);
			addDefaultRecipe(new ItemStack(Items.POTATO), new ItemStack(Items.POTATO, 3), new ItemStack(Items.POISONOUS_POTATO), 5);
			addDefaultRecipe(new ItemStack(Items.CARROT), new ItemStack(Items.CARROT, 3), null, 0);
			addDefaultRecipe(new ItemStack(Items.DYE, 1, 3), new ItemStack(Items.DYE, 3, 3), null, 0);
			addDefaultRecipe(new ItemStack(Items.REEDS), new ItemStack(Items.REEDS, 2), null, 0);
			addDefaultRecipe(new ItemStack(Blocks.CACTUS), new ItemStack(Blocks.CACTUS, 2), null, 0);
			addDefaultRecipe(new ItemStack(Blocks.VINE), new ItemStack(Blocks.VINE, 2), null, 0);
			addDefaultRecipe(new ItemStack(Blocks.WATERLILY), new ItemStack(Blocks.WATERLILY, 2), null, 0);
			addDefaultRecipe(new ItemStack(Items.PUMPKIN_SEEDS), new ItemStack(Blocks.PUMPKIN), null, 0);
			addDefaultRecipe(new ItemStack(Items.MELON_SEEDS), new ItemStack(Blocks.MELON_BLOCK), null, 0);
		}

		/* MYCELIUM */
		{
			ArrayList<ItemStack> crops = new ArrayList<>();
			crops.add(new ItemStack(Blocks.BROWN_MUSHROOM));
			crops.add(new ItemStack(Blocks.RED_MUSHROOM));

			for (ItemStack input : crops) {
				addRecipe(DEFAULT_ENERGY, input, ItemFertilizer.fertilizerBasic, ItemHelper.cloneStack(input, 2), null, 0, false, Substrate.MYCELIUM);
				addRecipe(DEFAULT_ENERGY_RICH, input, ItemFertilizer.fertilizerRich, ItemHelper.cloneStack(input, 2 * CROP_MULTIPLIER_RICH), null, 0, false, Substrate.MYCELIUM);
				addRecipe(DEFAULT_ENERGY_FLUX, input, ItemFertilizer.fertilizerFlux, ItemHelper.cloneStack(input, 2 * CROP_MULTIPLIER_FLUX), null, 0, false, Substrate.MYCELIUM);
			}
		}

		/* NETHER */
		{
			ArrayList<ItemStack> crops = new ArrayList<>();
			crops.add(new ItemStack(Items.NETHER_WART));

			for (ItemStack input : crops) {
				addRecipe(DEFAULT_ENERGY, input, ItemFertilizer.fertilizerBasic, ItemHelper.cloneStack(input, 2), null, 0, false, Substrate.NETHER);
				addRecipe(DEFAULT_ENERGY_RICH, input, ItemFertilizer.fertilizerRich, ItemHelper.cloneStack(input, 2 * CROP_MULTIPLIER_RICH), null, 0, false, Substrate.NETHER);
				addRecipe(DEFAULT_ENERGY_FLUX, input, ItemFertilizer.fertilizerFlux, ItemHelper.cloneStack(input, 2 * CROP_MULTIPLIER_FLUX), null, 0, false, Substrate.NETHER);
			}
		}

		/* END */
		{
			ItemStack input = new ItemStack(Blocks.CHORUS_FLOWER);
			ItemStack output = new ItemStack(Blocks.CHORUS_PLANT);

			addRecipe(DEFAULT_ENERGY, input, ItemFertilizer.fertilizerBasic, ItemHelper.cloneStack(output, 2), ItemHelper.cloneStack(input), 100, false, Substrate.END);
			addRecipe(DEFAULT_ENERGY_RICH, input, ItemFertilizer.fertilizerRich, ItemHelper.cloneStack(output, 2 * CROP_MULTIPLIER_RICH), ItemHelper.cloneStack(input), 100, false, Substrate.END);
			addRecipe(DEFAULT_ENERGY_FLUX, input, ItemFertilizer.fertilizerFlux, ItemHelper.cloneStack(output, 2 * CROP_MULTIPLIER_FLUX), ItemHelper.cloneStack(input), 150, false, Substrate.END);
		}
	}

	public static void loadRecipes() {

		String[] oreNameList = OreDictionary.getOreNames();
		String oreName;

		for (int i = 0; i < oreNameList.length; i++) {
			if (oreNameList[i].startsWith("seed")) {
				oreName = oreNameList[i].substring(4, oreNameList[i].length());
				addDefaultOreDictionaryRecipe(oreName);
			}
		}
	}

	public static void refreshRecipes() {

		Map<List<ComparableItemStackInsolator>, RecipeInsolator> tempMap = new THashMap<>(recipeMap.size());
		Set<ComparableItemStackInsolator> tempSet = new THashSet<>();
		RecipeInsolator tempRecipe;

		for (Entry<List<ComparableItemStackInsolator>, RecipeInsolator> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackInsolator primary = new ComparableItemStackInsolator(tempRecipe.primaryInput);
			ComparableItemStackInsolator secondary = new ComparableItemStackInsolator(tempRecipe.secondaryInput);

			tempMap.put(Arrays.asList(primary, secondary), tempRecipe);
			tempSet.add(primary);
			tempSet.add(secondary);
		}
		recipeMap.clear();
		recipeMap = tempMap;
		validationSet.clear();
		validationSet = tempSet;

		Set<ComparableItemStackInsolator> tempSet2 = new THashSet<>();
		for (ComparableItemStackInsolator entry : lockSet) {
			ComparableItemStackInsolator lock = new ComparableItemStackInsolator(new ItemStack(entry.item, entry.stackSize, entry.metadata));
			tempSet2.add(lock);
		}
		lockSet.clear();
		lockSet = tempSet2;
	}

	/* ADD RECIPES */
	public static boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, boolean copyNBT, Substrate substrate) {

		if (primaryInput == null || secondaryInput == null || energy <= 0 || recipeExists(primaryInput, secondaryInput)) {
			return false;
		}
		RecipeInsolator recipe = new RecipeInsolator(primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, energy, copyNBT, substrate);
		recipeMap.put(Arrays.asList(new ComparableItemStackInsolator(primaryInput), new ComparableItemStackInsolator(secondaryInput)), recipe);
		validationSet.add(new ComparableItemStackInsolator(primaryInput));
		validationSet.add(new ComparableItemStackInsolator(secondaryInput));
		return true;
	}

	public static boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, false, Substrate.STANDARD);
	}

	public static boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, 100);
	}

	public static boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, null, 0);
	}

	/* REMOVE RECIPES */
	public static boolean removeRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		return recipeMap.remove(Arrays.asList(new ComparableItemStackInsolator(primaryInput), new ComparableItemStackInsolator(secondaryInput))) != null;
	}

	/* HELPERS */
	private static void addFertilizer(ItemStack fertilizer) {

		lockSet.add(new ComparableItemStackInsolator(fertilizer));
	}

	private static void addDefaultOreDictionaryRecipe(String oreType) {

		if (oreType.length() <= 0) {
			return;
		}
		String seedName = "seed" + StringHelper.titleCase(oreType);
		String cropName = "crop" + StringHelper.titleCase(oreType);

		List<ItemStack> registeredSeed = OreDictionary.getOres(seedName);
		List<ItemStack> registeredCrop = OreDictionary.getOres(cropName);

		if (registeredSeed.isEmpty() || registeredCrop.isEmpty()) {
			return;
		}
		boolean isTuber = false;
		boolean isBlock = false;
		for (int i = 0; i < registeredSeed.size(); i++) {
			for (int j = 0; j < registeredCrop.size(); j++) {
				if (ItemHelper.itemsEqualWithMetadata(registeredSeed.get(i), registeredCrop.get(j))) {
					isTuber = true;
				}
			}
		}
		if (ItemHelper.isBlock(registeredCrop.get(0))) {
			isBlock = true;
		}
		ItemStack seed = ItemHelper.cloneStack(registeredSeed.get(0), 1);
		ItemStack crop = ItemHelper.cloneStack(registeredCrop.get(0), isTuber ? 3 : 1);

		if (isBlock || isTuber) {
			addDefaultRecipe(seed, crop, null, 0);
		} else {
			addDefaultRecipe(seed, crop, seed, 150);
		}
	}

	private static void addDefaultRecipe(ItemStack primaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (secondaryOutput != null) {
			addRecipe(DEFAULT_ENERGY, primaryInput, ItemFertilizer.fertilizerBasic, primaryOutput, secondaryOutput, secondaryChance);
			addRecipe(DEFAULT_ENERGY_RICH, primaryInput, ItemFertilizer.fertilizerRich, ItemHelper.cloneStack(primaryOutput, primaryOutput.stackSize * CROP_MULTIPLIER_RICH), secondaryOutput, secondaryChance < 100 ? 100 : secondaryChance);
			addRecipe(DEFAULT_ENERGY_FLUX, primaryInput, ItemFertilizer.fertilizerFlux, ItemHelper.cloneStack(primaryOutput, primaryOutput.stackSize * CROP_MULTIPLIER_FLUX), secondaryOutput, secondaryChance < 150 ? 150 : secondaryChance);
		} else {
			addRecipe(DEFAULT_ENERGY, primaryInput, ItemFertilizer.fertilizerBasic, primaryOutput);
			addRecipe(DEFAULT_ENERGY_RICH, primaryInput, ItemFertilizer.fertilizerRich, ItemHelper.cloneStack(primaryOutput, primaryOutput.stackSize * CROP_MULTIPLIER_RICH));
			addRecipe(DEFAULT_ENERGY_FLUX, primaryInput, ItemFertilizer.fertilizerFlux, ItemHelper.cloneStack(primaryOutput, primaryOutput.stackSize * CROP_MULTIPLIER_FLUX));
		}
	}

	/* RECIPE CLASS */
	public static class RecipeInsolator {

		final ItemStack primaryInput;
		final ItemStack secondaryInput;
		final ItemStack primaryOutput;
		final ItemStack secondaryOutput;
		final int secondaryChance;
		final int energy;
		final boolean copyNBT;
		final Substrate substrate;

		RecipeInsolator(ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int energy) {

			this(primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, energy, false, Substrate.STANDARD);
		}

		RecipeInsolator(ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int energy, boolean copyNBT, Substrate substrate) {

			this.primaryInput = primaryInput;
			this.secondaryInput = secondaryInput;
			this.primaryOutput = primaryOutput;
			this.secondaryOutput = secondaryOutput;
			this.secondaryChance = secondaryChance;
			this.energy = energy;
			this.copyNBT = copyNBT;
			this.substrate = substrate;

			if (primaryInput.stackSize <= 0) {
				primaryInput.stackSize = 1;
			}
			if (secondaryInput.stackSize <= 0) {
				secondaryInput.stackSize = 1;
			}
			if (primaryOutput.stackSize <= 0) {
				primaryOutput.stackSize = 1;
			}
			if (secondaryOutput != null && secondaryOutput.stackSize <= 0) {
				secondaryOutput.stackSize = 1;
			}
		}

		public ItemStack getPrimaryInput() {

			return primaryInput;
		}

		public ItemStack getSecondaryInput() {

			return secondaryInput;
		}

		public ItemStack getPrimaryOutput() {

			return primaryOutput;
		}

		public ItemStack getSecondaryOutput() {

			return secondaryOutput;
		}

		public int getSecondaryOutputChance() {

			return secondaryChance;
		}

		public int getEnergy() {

			return energy;
		}

		public Substrate getSubstrate() {

			return substrate;
		}
	}

	/* SUBSTRATE ENUM */
	public enum Substrate {
		STANDARD, MYCELIUM, NETHER, END
	}

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackInsolator extends ComparableItemStack {

		public static final String SEED = "seed";
		public static final String CROP = "crop";

		public static boolean safeOreType(String oreName) {

			return oreName.startsWith(SEED) || oreName.startsWith(CROP);
		}

		public static int getOreID(ItemStack stack) {

			ArrayList<Integer> ids = OreDictionaryArbiter.getAllOreIDs(stack);

			if (ids != null) {
				for (int i = 0, e = ids.size(); i < e; ) {
					int id = ids.get(i++);
					if (id != -1 && safeOreType(ItemHelper.oreProxy.getOreName(id))) {
						return id;
					}
				}
			}
			return -1;
		}

		public ComparableItemStackInsolator(ItemStack stack) {

			super(stack);
			oreID = getOreID(stack);
		}

		@Override
		public ComparableItemStackInsolator set(ItemStack stack) {

			super.set(stack);
			oreID = getOreID(stack);

			return this;
		}
	}

}
