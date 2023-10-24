package lassevkp.revivals.mixin;

import lassevkp.revivals.Revivals;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Shadow public abstract boolean changeGameMode(GameMode gameMode);

    @Shadow @Final public ServerPlayerInteractionManager interactionManager;
    @Shadow @Final public MinecraftServer server;
    @Unique
    private boolean revivalsDeadMode;

    @Unique
    public boolean getRevivalsDeadMode() {
        return this.revivalsDeadMode;
    }


    @Unique
    public void enableRevivalsDeadMode() {
        this.revivalsDeadMode = true;
        Revivals.LOGGER.info("Fucking dead man, stupid");
        this.changeGameMode(GameMode.SPECTATOR);

    }

    @Unique
    public void disableRevivalsDeadMode() {
        this.revivalsDeadMode = false;
    }

    @Inject(method = "onDeath", at=@At("HEAD"), cancellable = true)
    private void onDeath(DamageSource damageSource, CallbackInfo ci){
        if(this.server.isHardcore() && this.interactionManager.getGameMode() == GameMode.SURVIVAL){

            enableRevivalsDeadMode();
            ci.cancel();
        }
    }

}
