package lassevkp.revivals.block;

import lassevkp.revivals.Revivals;
import lassevkp.revivals.blockEntity.RitualTableEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RitualTableBlock extends LecternBlock{
    protected RitualTableBlock(Settings settings){
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state){
        return new RitualTableEntity(pos, state);
    }

    private void openScreen(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {


    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

            if(screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }

        return ActionResult.SUCCESS;
    }
}
