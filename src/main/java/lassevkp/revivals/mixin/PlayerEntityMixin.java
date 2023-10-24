package lassevkp.revivals.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "onDeath", at=@At("HEAD"), cancellable = true)
    private void onDeath(DamageSource damageSource, CallbackInfo ci){
        PlayerEntity player = (PlayerEntity) (Object) this;
        MinecraftServer server = player.getServer();

        if(server.isHardcore()){
            ci.cancel();
        }
    }


}
