package io.github.freefull.exmateria.server.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CrucibleBlock extends BlockWithEntity {
    protected static final VoxelShape OUTLINE_SHAPE = VoxelShapes.union(
            createCuboidShape(1.0, 0.0, 1.0, 15.0, 1.0, 15.0), createCuboidShape(0.0, 1.0, 1.0, 2.0, 16.0, 15.0),
            createCuboidShape(14.0, 1.0, 1.0, 16.0, 16.0, 15.0), createCuboidShape(1.0, 1.0, 14.0, 15.0, 16.0, 16.0),
            createCuboidShape(1.0, 1.0, 0.0, 15.0, 16.0, 2.0));

    public CrucibleBlock(Settings settings) {
        super(settings);
    }

    private CrucibleBlockEntity getEntity(World world, BlockPos blockPos) {
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if(blockEntity instanceof CrucibleBlockEntity) {
            return (CrucibleBlockEntity)blockEntity;
        }
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean isFullBoundsCubeForCulling(BlockState blockState) {
        return false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1,
            EntityContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand,
            BlockHitResult blockHitResult) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        CrucibleBlockEntity blockEntity = getEntity(world, blockPos);
        if(blockEntity == null) {
            return false;
        }
        blockEntity.dropOutput();

        if (itemStack.isEmpty() || world.isClient) {
            return true;
        } else {
            BlockState blockStateBelow = world.getBlockState(blockPos.add(0, -1, 0));
            Block blockBelow = blockStateBelow.getBlock();
            // TODO: Replace with JSON
            boolean hasHeat = blockBelow == Blocks.FIRE || blockBelow == Blocks.LAVA || blockBelow == Blocks.MAGMA_BLOCK
                    || (blockBelow == Blocks.CAMPFIRE && blockStateBelow.get(CampfireBlock.LIT));
            return blockEntity.insertItems(itemStack);
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockView var1) {
        return new CrucibleBlockEntity();
    }
}