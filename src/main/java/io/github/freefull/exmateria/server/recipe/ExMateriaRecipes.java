package io.github.freefull.exmateria.server.recipe;

import io.github.freefull.exmateria.ExMateria;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ExMateriaRecipes {
    public static RecipeType<CrucibleRecipe> CRUCIBLE_RECIPE = register("crucible");
    public static RecipeSerializer<CrucibleRecipe> CRUCIBLE_SERIALIZER = register("crucible", CrucibleRecipe.SERIALIZER);

    public static void init() {
    }

    private static <T extends Recipe<?>> RecipeType<T> register(String id) {
        return Registry.register(Registry.RECIPE_TYPE, new Identifier(ExMateria.MOD_ID, id), new RecipeType<T>() {
            public String toString() {
                return id;
            }
        });
    }

    private static <T extends Recipe<?>> RecipeSerializer<T> register(String id, RecipeSerializer<T> serializer) {
        return Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(ExMateria.MOD_ID, id), serializer);
    }
}