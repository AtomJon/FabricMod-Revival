package lassevkp.revivals;

<<<<<<< Updated upstream
import lassevkp.revivals.PersistentPlayerList.PersistentDeadPlayerList;
=======
import io.netty.channel.ChannelHandler;
>>>>>>> Stashed changes
import lassevkp.revivals.block.ModBlocks;
import lassevkp.revivals.block.entity.ModBlockEntities;
import lassevkp.revivals.item.ModItems;
import lassevkp.revivals.screen.ModScreenHandlers;
import lassevkp.revivals.screen.RitualTableScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

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
				Revivals::AllowDeathEventListener
		);

<<<<<<< Updated upstream
		ServerPlayConnectionEvents.JOIN.register(
			Revivals::PlayerJoinedEventListener
		);
	}

	private static boolean AllowDeathEventListener(LivingEntity entity, DamageSource damageSource, float damageAmount) {
		if (!(entity instanceof ServerPlayerEntity)) return true;
        ServerPlayerEntity player = (ServerPlayerEntity) entity;

        if (player.interactionManager.getGameMode() != GameMode.SURVIVAL) return true;

		player.changeGameMode(GameMode.SPECTATOR);

		LOGGER.info("Making this guy not die... ( ͡° ͜ʖ ͡°)");
		resetPlayerHealth(entity);
		makePlayerDropItems(entity, damageSource);

		MinecraftServer server = player.getServer();
		assert server != null;

		AddPlayerToDeadPlayers(player, server);

		TellClientPlayerIsDead(player, server);

		return false;
	}

	private static void PlayerJoinedEventListener(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
		ServerPlayerEntity player = handler.player;
		LOGGER.info("A new player joined: " + player.getName());

		PersistentDeadPlayerList state = PersistentDeadPlayerList.getServerDeadPlayerList(server);
		boolean playerIsDead = state.isPlayerUUIDDead(player.getUuid());

		if (playerIsDead)
		{
			// Send a packet to the client
			TellClientPlayerIsDead(player, server);
		}
	}

	private static void AddPlayerToDeadPlayers(ServerPlayerEntity player, MinecraftServer server) {
		PersistentDeadPlayerList state = PersistentDeadPlayerList.getServerDeadPlayerList(server);
		state.setPlayerIsDead(player.getUuid());
	}

	private static void TellClientPlayerIsDead(ServerPlayerEntity player, MinecraftServer server) {
		// Send a packet to the client
		PacketByteBuf data = PacketByteBufs.create();
		data.writeUuid(player.getUuid());

		server.execute(() ->
			ServerPlayNetworking.send(player, PLAYER_IS_DEAD, data)
		);
	}

	private static void makePlayerDropItems(LivingEntity entity, DamageSource damageSource) {
		try {
			Method drop = LivingEntity.class.getDeclaredMethod("drop", DamageSource.class);
			drop.setAccessible(true);
			drop.invoke(entity, damageSource);
		} catch (Exception e){
			LOGGER.error(e.toString());
		}
	}

	private static void resetPlayerHealth(LivingEntity entity) {
		entity.setHealth(20.0f);
=======
		ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.RESURRECT_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			if(player.currentScreenHandler instanceof RitualTableScreenHandler){
				RitualTableScreenHandler ritualTableScreenHandler = (RitualTableScreenHandler) player.currentScreenHandler;
				ritualTableScreenHandler.tryRevive(buf.readUuid(), player);

			}
		});

>>>>>>> Stashed changes
	}
}