package io.github.freefull.exmateria.server.recipe;

import com.google.gson.JsonObject;

import io.github.freefull.exmateria.server.block.CrucibleBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;

public class CrucibleRecipe implements Recipe<CrucibleBlockEntity> {
    private Identifier id;
    private boolean needsHeat;
    private Ingredient ingredient;
    private ItemStack result;

    public static Serializer SERIALIZER = new Serializer();

    public CrucibleRecipe(Identifier id, boolean needsHeat, Ingredient ingredient, ItemStack result) {
        this.id = id;
        this.needsHeat = needsHeat;
        this.ingredient = ingredient;
        this.result = result;
    }

    @Override
    public boolean matches(CrucibleBlockEntity inventory, World world) {
        return ingredient.test(inventory.getInvStack(0)) && (inventory.isHot() == needsHeat);
    }

    @Override
    public ItemStack craft(CrucibleBlockEntity inventory) {
        return result;
    }

    @Override
    public boolean fits(int var1, int var2) {
        return false;
    }

    @Override
    public ItemStack getOutput() {
        return result;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<CrucibleRecipe> getSerializer() {
        return ExMateriaRecipes.CRUCIBLE_SERIALIZER;
    }

    @Override
    public RecipeType<CrucibleRecipe> getType() {
        return ExMateriaRecipes.CRUCIBLE_RECIPE;
    }

    public static class Serializer implements RecipeSerializer<CrucibleRecipe> {
        @Override
        public CrucibleRecipe read(Identifier id, JsonObject json) {
            boolean needsHeat = JsonHelper.getBoolean(json, "needs_heat");
            JsonObject ingredient = JsonHelper.getObject(json, "ingredient");
            JsonObject result = JsonHelper.getObject(json, "result");
            return new CrucibleRecipe(id, needsHeat, Ingredient.fromJson(ingredient),
                    ShapedRecipe.getItemStack(result));
        }

        @Override
        public CrucibleRecipe read(Identifier id, PacketByteBuf buf) {
            boolean needsHeat = buf.readBoolean();
            Ingredient ingredient = Ingredient.fromPacket(buf);
            ItemStack result = buf.readItemStack();
            return new CrucibleRecipe(id, needsHeat, ingredient, result);
        }

        @Override
        public void write(PacketByteBuf buf, CrucibleRecipe recipe) {
            buf.writeBoolean(recipe.needsHeat);
            recipe.ingredient.write(buf);
            buf.writeItemStack(recipe.result);
        }
    }
}