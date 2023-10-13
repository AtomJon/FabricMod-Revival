package lassevkp.revivals.block;

import lassevkp.revivals.Revivals;
import lassevkp.revivals.screenHandler.RitualTableScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RitualTable extends LecternBlock{

    public RitualTable(Settings settings) {
        super(settings);
    }

    private void openScreen(World world, BlockPos pos, PlayerEntity player) {
        // Still need to find out how to do this

    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Revivals.LOGGER.info("clicked thingy");
        this.openScreen(world, pos, player);
        return ActionResult.success(world.isClient);
    }
}
