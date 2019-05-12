package io.github.freefull.exmateria;

import io.github.freefull.exmateria.server.block.CrucibleBlock;
import io.github.freefull.exmateria.server.recipe.ExMateriaRecipes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ExMateria implements ModInitializer {
	public static final String MOD_ID = "exmateria";

	public static final Block CRUCIBLE = new CrucibleBlock(FabricBlockSettings.of(Material.METAL).build());

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "crucible"), CRUCIBLE);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "crucible"), new BlockItem(CRUCIBLE, new Item.Settings().itemGroup(ItemGroup.MISC)));

		ExMateriaRecipes.init();
	}
}
