package lassevkp.revivals.mixin;

import lassevkp.revivals.Revivals;
import lassevkp.revivals.common.HasRevivalsDeadState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements HasRevivalsDeadState {
    @Shadow public abstract boolean changeGameMode(GameMode gameMode);

    @Shadow @Final public ServerPlayerInteractionManager interactionManager;
    @Shadow @Final public MinecraftServer server;

    @Override
    public boolean getRevivalsDeadState(){
        return revivalsDeadMode;
    }

    @Unique
    private boolean revivalsDeadMode;

    /*@Unique
    public boolean getRevivalsDeadMode() {
        return this.revivalsDeadMode;
    }*/

    @Unique
    public void enableRevivalsDeadMode() {
        this.revivalsDeadMode = true;
        this.changeGameMode(GameMode.SPECTATOR);
    }

    @Unique
    public void disableRevivalsDeadMode() {
        this.revivalsDeadMode = false;
    }



    @Inject(method="onDeath",at=@At("HEAD"),cancellable = true)
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        if (!this.server.isHardcore() || this.interactionManager.getGameMode() != GameMode.SURVIVAL) return;

    }

}
