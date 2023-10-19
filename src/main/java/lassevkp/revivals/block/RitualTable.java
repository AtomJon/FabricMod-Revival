package lassevkp.revivals.block;

import lassevkp.revivals.block.entity.RitualTableBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RitualTable extends BlockWithEntity implements BlockEntityProvider {
    public static final DirectionProperty FACING;
    public static final VoxelShape BOTTOM_SHAPE;
    public static final VoxelShape MIDDLE_SHAPE;
    public static final VoxelShape BASE_SHAPE;
    public static final VoxelShape COLLISION_SHAPE_TOP;
    public static final VoxelShape COLLISION_SHAPE;
    public static final VoxelShape WEST_SHAPE;
    public static final VoxelShape NORTH_SHAPE;
    public static final VoxelShape EAST_SHAPE;
    public static final VoxelShape SOUTH_SHAPE;


    public RitualTable(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH))));
    }

    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return BASE_SHAPE;
    }

    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        ItemStack itemStack = ctx.getStack();
        PlayerEntity playerEntity = ctx.getPlayer();
        boolean bl = false;
        if (!world.isClient && playerEntity != null && playerEntity.isCreativeLevelTwoOp()) {
            NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(itemStack);
            if (nbtCompound != null && nbtCompound.contains("Book")) {
                bl = true;
            }
        }

        return (BlockState)((BlockState)this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()));
    }

    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch ((Direction)state.get(FACING)) {
            case NORTH:
                return NORTH_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case EAST:
                return EAST_SHAPE;
            case WEST:
                return WEST_SHAPE;
            default:
                return BASE_SHAPE;
        }
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RitualTableBlockEntity) {
                ItemScatterer.spawn(world, pos, (RitualTableBlockEntity)blockEntity);
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RitualTableBlockEntity(pos, state);
    }

    static {
        FACING = HorizontalFacingBlock.FACING;
        BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        MIDDLE_SHAPE = Block.createCuboidShape(4.0, 2.0, 4.0, 12.0, 14.0, 12.0);
        BASE_SHAPE = VoxelShapes.union(BOTTOM_SHAPE, MIDDLE_SHAPE);
        COLLISION_SHAPE_TOP = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 15.0, 16.0);
        COLLISION_SHAPE = VoxelShapes.union(BASE_SHAPE, COLLISION_SHAPE_TOP);
        WEST_SHAPE = VoxelShapes.union(Block.createCuboidShape(1.0, 10.0, 0.0, 5.333333, 14.0, 16.0), new VoxelShape[]{Block.createCuboidShape(5.333333, 12.0, 0.0, 9.666667, 16.0, 16.0), Block.createCuboidShape(9.666667, 14.0, 0.0, 14.0, 18.0, 16.0), BASE_SHAPE});
        NORTH_SHAPE = VoxelShapes.union(Block.createCuboidShape(0.0, 10.0, 1.0, 16.0, 14.0, 5.333333), new VoxelShape[]{Block.createCuboidShape(0.0, 12.0, 5.333333, 16.0, 16.0, 9.666667), Block.createCuboidShape(0.0, 14.0, 9.666667, 16.0, 18.0, 14.0), BASE_SHAPE});
        EAST_SHAPE = VoxelShapes.union(Block.createCuboidShape(10.666667, 10.0, 0.0, 15.0, 14.0, 16.0), new VoxelShape[]{Block.createCuboidShape(6.333333, 12.0, 0.0, 10.666667, 16.0, 16.0), Block.createCuboidShape(2.0, 14.0, 0.0, 6.333333, 18.0, 16.0), BASE_SHAPE});
        SOUTH_SHAPE = VoxelShapes.union(Block.createCuboidShape(0.0, 10.0, 10.666667, 16.0, 14.0, 15.0), new VoxelShape[]{Block.createCuboidShape(0.0, 12.0, 6.333333, 16.0, 16.0, 10.666667), Block.createCuboidShape(0.0, 14.0, 2.0, 16.0, 18.0, 6.333333), BASE_SHAPE});
    }

}
