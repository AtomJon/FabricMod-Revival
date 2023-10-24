package lassevkp.revivals.mixin;

import lassevkp.revivals.Revivals;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
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
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements ServerLivingEntityEvents.AllowDeath {
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



    @Inject(method="onDeath",at=@At("HEAD"),cancellable = true)
    private void onDeath(DamageSource damageSource, CallbackInfo ci){
        if(!this.server.isHardcore() || this.interactionManager.getGameMode() != GameMode.SURVIVAL) return;
        try {
            Method onKilledBy = LivingEntity.class.getDeclaredMethod("onKilledBy", LivingEntity.class);
            onKilledBy.setAccessible(true);

            Method drop = LivingEntity.class.getDeclaredMethod("drop", DamageSource.class);
            drop.setAccessible(true);

            Method dropShoulderEntities = PlayerEntity.class.getDeclaredMethod("dropShoulderEntities");
            dropShoulderEntities.setAccessible(true);

            Method forgiveMobAnger = ServerPlayerEntity.class.getDeclaredMethod("forgiveMobAnger");
            forgiveMobAnger.setAccessible(true);

            Field scoreAmount = LivingEntity.class.getDeclaredField("scoreAmount");
            scoreAmount.setAccessible(true);

            ServerPlayerEntity e = (ServerPlayerEntity) (Object) this;

            e.setHealth(20.0f);

            e.emitGameEvent(GameEvent.ENTITY_DIE);
            boolean bl = e.getWorld().getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES);
            if (bl) {
                Text text = e.getDamageTracker().getDeathMessage();
                AbstractTeam abstractTeam = e.getScoreboardTeam();
                if (abstractTeam != null && abstractTeam.getDeathMessageVisibilityRule() != AbstractTeam.VisibilityRule.ALWAYS) {
                    if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS) {
                        this.server.getPlayerManager().sendToTeam(e, text);
                    } else if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM) {
                        this.server.getPlayerManager().sendToOtherTeams(e, text);
                    }
                } else {
                    this.server.getPlayerManager().broadcast(text, false);
                }
            } else {
                e.networkHandler.sendPacket(new DeathMessageS2CPacket(e.getId(), ScreenTexts.EMPTY));
            }

            dropShoulderEntities.invoke(this);
            if (e.getWorld().getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
                forgiveMobAnger.invoke(this);
            }

            if (!e.isSpectator()) {
                drop.invoke(this,damageSource);
            }

            e.getScoreboard().forEachScore(ScoreboardCriterion.DEATH_COUNT, e.getEntityName(), ScoreboardPlayerScore::incrementScore);
            LivingEntity livingEntity = e.getPrimeAdversary();
            if (livingEntity != null) {
                e.incrementStat(Stats.KILLED_BY.getOrCreateStat(livingEntity.getType()));
                livingEntity.updateKilledAdvancementCriterion(e, (int) scoreAmount.get(this), damageSource);
                onKilledBy.invoke(this, livingEntity);
            }

            e.getWorld().sendEntityStatus(e, (byte)3);
            e.incrementStat(Stats.DEATHS);
            e.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_DEATH));
            e.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
            e.extinguish();
            e.setFrozenTicks(0);
            e.setOnFire(false);
            e.getDamageTracker().update();
            e.setLastDeathPos(Optional.of(GlobalPos.create(e.getWorld().getRegistryKey(), e.getBlockPos())));

            enableRevivalsDeadMode();

            ci.cancel();

        } catch (Exception e) {
            Revivals.LOGGER.error(e.toString());
        }
    }

}
