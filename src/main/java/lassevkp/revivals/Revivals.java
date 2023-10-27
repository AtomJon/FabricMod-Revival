package lassevkp.revivals;

import lassevkp.revivals.block.ModBlocks;
import lassevkp.revivals.block.entity.ModBlockEntities;
import lassevkp.revivals.item.ModItems;
import lassevkp.revivals.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.UUID;

public class Revivals implements ModInitializer {

	public static final String MOD_ID = "revivals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier PLAYER_IS_DEAD = new Identifier(MOD_ID, "player_is_dead");

	@Override
	public void onInitialize() {

		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerAllScreenHandlers();

		LOGGER.info("Revivals is initialized! =D");

		ServerLivingEntityEvents.ALLOW_DEATH.register(
				(LivingEntity entity, DamageSource damageSource, float damageAmount) -> {

					if (!(entity instanceof ServerPlayerEntity player)) return true;
					//if (!entity.getServer().isHardcore()) return true;

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

					UUID playerUuid = player.getUuid();


					MinecraftServer server = player.getServer();
					assert server != null;

					StateSaverAndLoader state = StateSaverAndLoader.getServerState(server);

					state.deadPlayers.add(player.getUuid());

					// Send a packet to the client
					PacketByteBuf data = PacketByteBufs.create();
					data.writeUuid(playerUuid);

                    ServerPlayerEntity playerEntity = server.getPlayerManager().getPlayer(player.getUuid());
					assert playerEntity != null;

					server.execute(() ->
                        ServerPlayNetworking.send(playerEntity, PLAYER_IS_DEAD, data)
					);

                    return false;
				}
		);

	}
}