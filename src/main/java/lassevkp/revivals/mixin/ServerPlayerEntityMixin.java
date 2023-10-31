package lassevkp.revivals.mixin;

import lassevkp.revivals.common.HasRevivalsDeadState;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements HasRevivalsDeadState {

}
