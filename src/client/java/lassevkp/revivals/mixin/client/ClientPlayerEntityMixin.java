package lassevkp.revivals.mixin.client;

import lassevkp.revivals.Revivals;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Shadow @Final protected MinecraftClient client;

    @Unique() boolean isDead = false;
    @Unique() Vec3d positionOfDeath = Vec3d.ZERO;

    @Unique
    boolean shouldNotMove()
    {
        assert client.interactionManager != null;

        // If player isn't in spectator mode, we can assume that they are not supposed to be dead and reset the state.
        if (client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR)
        {
            //isDead = false;
            return false;
        }

        return isDead;
    }

    @Inject(at = @At("TAIL"), method = "init()V")
    private void run(CallbackInfo info) {
        ClientPlayNetworking.registerGlobalReceiver(Revivals.PLAYER_IS_DEAD, (client, handler, buf, responseSender) -> {
            UUID deadPlayer = buf.readUuid();
            client.execute(() -> {
                assert client.player != null;
                client.player.sendMessage(Text.literal("Btw, this guy is dead: " + deadPlayer));

                boolean clientPlayerIsDead = client.player.getUuid().equals(deadPlayer);
                if (clientPlayerIsDead)
                {
                    isDead = true;
                    positionOfDeath = client.player.getPos();

                    client.player.sendMessage(Text.literal("Holy shit I'm dead!"));
                    Revivals.LOGGER.info("Sooo.... apparently i died?");
                }
            });
        });
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    public void tickMovement(CallbackInfo ci) {
        if (shouldNotMove()) {
            ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;

            player.setPosition(positionOfDeath);
            player.setVelocity(Vec3d.ZERO);
        }
    }
}
