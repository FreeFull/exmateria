package io.github.freefull.exmateria.server.block;

import io.github.freefull.exmateria.ExMateriaProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CrucibleBlock extends BlockWithEntity {
    protected static final VoxelShape OUTLINE_SHAPE = VoxelShapes.union(
            createCuboidShape(1.0, 0.0, 1.0, 15.0, 1.0, 15.0), createCuboidShape(0.0, 1.0, 1.0, 2.0, 16.0, 15.0),
            createCuboidShape(14.0, 1.0, 1.0, 16.0, 16.0, 15.0), createCuboidShape(1.0, 1.0, 14.0, 15.0, 16.0, 16.0),
            createCuboidShape(1.0, 1.0, 0.0, 15.0, 16.0, 2.0));

    public static final BooleanProperty HOT = ExMateriaProperties.HOT;

    public CrucibleBlock(Settings settings) {
        super(settings);
        setDefaultState(stateFactory.getDefaultState().with(HOT, false));
    }

    private CrucibleBlockEntity getEntity(World world, BlockPos blockPos) {
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof CrucibleBlockEntity) {
            return (CrucibleBlockEntity) blockEntity;
        }
        return null;
    }

    private boolean isBlockHeating(BlockState block) {
        return block.getBlock() == Blocks.CAMPFIRE && block.get(CampfireBlock.LIT);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> stateFactory) {
        super.appendProperties(stateFactory);
        stateFactory.add(HOT);
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaque(BlockState blockState) {
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
        if (blockEntity == null) {
            return false;
        }
        blockEntity.dropOutput();

        if (itemStack.isEmpty() || world.isClient) {
            return true;
        } else {
            return blockEntity.insertItems(itemStack);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockState blockBelow = context.getWorld().getBlockState(context.getBlockPos().down());
        return this.getDefaultState().with(HOT, isBlockHeating(blockBelow));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState_1, Direction dir, BlockState blockState_2,
            IWorld iWorld, BlockPos blockPos_1, BlockPos blockPos_2) {
        return dir == Direction.DOWN ? blockState_1.with(HOT, isBlockHeating(blockState_2))
                : super.getStateForNeighborUpdate(blockState_1, dir, blockState_2, iWorld, blockPos_1, blockPos_2);
    }

    @Override
    public void onBlockRemoved(BlockState blockState_1, World world_1, BlockPos blockPos_1, BlockState blockState_2,
            boolean boolean_1) {
        CrucibleBlockEntity blockEntity = getEntity(world_1, blockPos_1);
        if(blockEntity != null) {
            ItemScatterer.spawn(world_1, blockPos_1, blockEntity);
            world_1.updateHorizontalAdjacent(blockPos_1, this);
        }
        super.onBlockRemoved(blockState_1, world_1, blockPos_1, blockState_2, boolean_1);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView var1) {
        return new CrucibleBlockEntity();
    }
}