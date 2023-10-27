package lassevkp.revivals.mixin;

import lassevkp.revivals.StateSaverAndLoader;
import lassevkp.revivals.common.HasRevivalsDeadState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements HasRevivalsDeadState{

    @Shadow public abstract Text getDisplayName();

    @Unique
    private boolean revivalsDeadMode;

    @Unique
    public boolean getRevivalsDeadMode() {
        return this.revivalsDeadMode;
    }

    @Unique
    public void setRevivalsDeadMode(boolean state) {
        this.revivalsDeadMode = state;
    }

    @Shadow public abstract int getScore();

    @Inject(method = "tickMovement", at=@At("HEAD"), cancellable = true)
    public void tickMovement(CallbackInfo ci){
        PlayerEntity player = (PlayerEntity) (Object) this;


        // If this is on the server
        if(player.getServer() != null){
            StateSaverAndLoader state = StateSaverAndLoader.getServerState(player.getServer());

            if(state.deadPlayers.contains(player.getUuid())) ci.cancel();
        }

    }
}