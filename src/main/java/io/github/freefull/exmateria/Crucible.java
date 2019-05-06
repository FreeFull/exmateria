package io.github.freefull.exmateria;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class Crucible extends Block {
    protected static final VoxelShape OUTLINE_SHAPE;

    public Crucible(Settings settings) {
        super(settings);
    }

    public boolean isFullBoundsCubeForCulling(BlockState blockState) {
        return false;
    }

    public VoxelShape getOutlineShape(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1,
            VerticalEntityPosition verticalEntityPosition_1) {
        return OUTLINE_SHAPE;
    }

    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand,
            BlockHitResult blockHitResult) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    static {
        OUTLINE_SHAPE = VoxelShapes.union(
            createCuboidShape(1.0, 0.0, 1.0, 15.0, 1.0, 15.0),
            createCuboidShape(0.0, 1.0, 1.0, 2.0, 16.0, 15.0),
            createCuboidShape(14.0, 1.0, 1.0, 16.0, 16.0, 15.0),
            createCuboidShape(1.0, 1.0, 14.0, 15.0, 16.0, 16.0),
            createCuboidShape(1.0, 1.0, 0.0, 15.0, 16.0, 2.0));
    }
}