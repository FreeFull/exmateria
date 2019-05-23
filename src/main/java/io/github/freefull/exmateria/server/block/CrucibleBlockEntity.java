package io.github.freefull.exmateria.server.block;

import io.github.freefull.exmateria.ExMateria;
import io.github.freefull.exmateria.ExMateriaProperties;
import io.github.freefull.exmateria.server.recipe.CrucibleRecipe;
import io.github.freefull.exmateria.server.recipe.ExMateriaRecipes;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class CrucibleBlockEntity extends BlockEntity implements SidedInventory, Tickable {
    static final BlockEntityType<CrucibleBlockEntity> TYPE = Registry.register(Registry.BLOCK_ENTITY,
            "exmateria:crucible",
            BlockEntityType.Builder.create(CrucibleBlockEntity::new, ExMateria.CRUCIBLE).build(null));
    private final DefaultedList<ItemStack> inventory = DefaultedList.create(2, ItemStack.EMPTY);
    private static final int[] AVAILABLE_SLOTS = { 0, 1 };
    private CrucibleRecipe cachedRecipe;

    public CrucibleBlockEntity() {
        super(TYPE);
    }

    public boolean insertItems(ItemStack items) {
        ItemStack input = inventory.get(0);
        if (input.isEmpty()) {
            inventory.set(0, items.split(1));
            return true;
        } else if (input.isEqualIgnoreTags(items) && input.getAmount() < getInvMaxStackAmount()) {
            input.addAmount(1);
            items.subtractAmount(1);
            return true;
        }
        System.out.print(inventory.get(0));
        return false;
    }

    public boolean isHot() {
        return getCachedState().get(ExMateriaProperties.HOT);
    }

    private CrucibleRecipe getCachedRecipe() {
        if (cachedRecipe != null && cachedRecipe.matches(this, world)) {
            return cachedRecipe;
        } else {
            cachedRecipe = world.getRecipeManager().getFirstMatch(ExMateriaRecipes.CRUCIBLE_RECIPE, this, world)
                    .orElse(null);
            return cachedRecipe;
        }
    }

    private boolean canAcceptRecipeOutput(CrucibleRecipe recipe) {
        ItemStack recipeOutput = recipe.getOutput();
        if (recipeOutput.isEmpty()) {
            return false;
        }
        ItemStack output = inventory.get(1);
        if (output.isEmpty()) {
            return true;
        } else if (!output.isEqualIgnoreTags(recipeOutput)) {
            return false;
        } else if (output.getAmount() < this.getInvMaxStackAmount() && output.getAmount() < output.getMaxAmount()) {
            return true;
        } else {
            return output.getAmount() < recipeOutput.getMaxAmount();
        }
    }

    private void craftRecipe(CrucibleRecipe recipe) {
        if (canAcceptRecipeOutput(recipe)) {
            ItemStack input = inventory.get(0);
            ItemStack output = inventory.get(1);
            ItemStack recipeOutput = recipe.craft(this);
            if (output.isEmpty()) {
                inventory.set(1, recipeOutput.copy());
            } else if (output.getItem() == recipeOutput.getItem()) {
                output.addAmount(recipeOutput.getAmount());
            }
            input.subtractAmount(1);
        }
    }

    public void dropOutput() {
        dropItems(inventory.get(1));
    }

    private void dropItems(ItemStack items) {
        ItemStack toSpitOut = items.copy();
        items.setAmount(0);
        BlockPos pos = this.getPos();
        ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, toSpitOut);
        world.spawnEntity(itemEntity);
    }

    @Override
    public void clear() {
        for (ItemStack stack : inventory) {
            stack.setAmount(0);
        }
    }

    @Override
    public int getInvSize() {
        return this.inventory.size();
    }

    @Override
    public boolean isInvEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getInvStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack takeInvStack(int slot, int amount) {
        return Inventories.splitStack(inventory, slot, amount);
    }

    @Override
    public ItemStack removeInvStack(int slot) {
        return Inventories.removeStack(inventory, slot);
    }

    @Override
    public void setInvStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity player) {
        return false;
    }

    @Override
    public int getInvMaxStackAmount() {
        return 4;
    }

    @Override
    public void tick() {
        if (!world.isClient) {
            ItemStack input = inventory.get(0);
            if (input.isEmpty()) {
                return;
            }
            CrucibleRecipe recipe = getCachedRecipe();
            if (recipe == null) {
                dropItems(input);
                return;
            }
            craftRecipe(recipe);
        }
    }

    @Override
    public int[] getInvAvailableSlots(Direction dir) {
        return AVAILABLE_SLOTS;
    }

    @Override
    public boolean canInsertInvStack(int slot, ItemStack stack, Direction dir) {
        return slot == 0 && inventory.get(0).isEmpty();
    }

    @Override
    public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
        return slot == 1 && !inventory.get(1).isEmpty();
    }
}