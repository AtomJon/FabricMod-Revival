package lassevkp.revivals;

import lassevkp.revivals.block.ModBlocks;
import lassevkp.revivals.block.entity.ModBlockEntities;
import lassevkp.revivals.item.ModItems;
import lassevkp.revivals.mixin.PlayerEntityMixin;
import lassevkp.revivals.mixin.ServerPlayerEntityMixin;
import lassevkp.revivals.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class Revivals implements ModInitializer {

	public static final String MOD_ID = "revivals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static DeadManager deadManager = new DeadManager();

	@Override
	public void onInitialize() {

		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerAllScreenHandlers();

		LOGGER.info("Revivals is initialized! =D");

		ServerLivingEntityEvents.ALLOW_DEATH.register(
				(LivingEntity entity, DamageSource damageSource, float damageAmount) -> {

					if (!(entity instanceof ServerPlayerEntity)) return true;
					//if (!entity.getServer().isHardcore()) return true;

					ServerPlayerEntity player = (ServerPlayerEntity) entity;

					if (player.interactionManager.getGameMode() != GameMode.SURVIVAL) return true;

					player.changeGameMode(GameMode.SPECTATOR);

					LOGGER.info("Making this guy not die... ( ͡° ͜ʖ ͡°)");
					entity.setHealth(20.0f);

					try{
						Method drop = LivingEntity.class.getDeclaredMethod("drop", DamageSource.class);
						drop.setAccessible(true);
						drop.invoke(entity, damageSource);
					} catch (Exception e){
						Revivals.LOGGER.error(e.toString());
					}

					player.getUuid();

					StateSaverAndLoader state = StateSaverAndLoader.getServerState(player.getServer());

					state.deadPlayers.add(player.getUuid());

					GameRules gamerules = player.getWorld().getGameRules();


					return false;


				}
		);

	}
}