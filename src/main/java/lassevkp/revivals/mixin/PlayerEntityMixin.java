package lassevkp.revivals.mixin;

import lassevkp.revivals.common.HasRevivalsDeadState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements HasRevivalsDeadState{

    @Shadow public abstract Text getDisplayName();

    @Inject(method = "tickMovement", at=@At("HEAD"), cancellable = true)
    public void tickMovement(CallbackInfo ci){
        if(this.getRevivalsDeadState()) ci.cancel();
        System.out.println(this.getDisplayName().getString());
        System.out.println(this.getRevivalsDeadState());
    }
}