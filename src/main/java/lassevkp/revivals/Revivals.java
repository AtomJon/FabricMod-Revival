package lassevkp.revivals;

import lassevkp.revivals.PersistentPlayerList.PersistentDeadPlayerList;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.GameMode;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.event.GameEvent;
import org.apache.logging.log4j.core.jmx.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

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

		LOGGER.info("Initializing Revivals");

		ServerLivingEntityEvents.ALLOW_DEATH.register(
				Revivals::AllowDeathEventListener
		);

		ServerPlayConnectionEvents.JOIN.register(
			Revivals::PlayerJoinedEventListener
		);
	}

	private static boolean AllowDeathEventListener(LivingEntity entity, DamageSource damageSource, float damageAmount) {
		if (!(entity instanceof ServerPlayerEntity)) return true;
        ServerPlayerEntity player = (ServerPlayerEntity) entity;

        if (player.interactionManager.getGameMode() != GameMode.SURVIVAL) return true;

		MinecraftServer server = player.getServer();
		ServerWorld world = player.getServerWorld();
		assert server != null;
		assert world != null;

		sendDeathMessage(player, world, server);

		player.changeGameMode(GameMode.SPECTATOR);

		resetPlayerHealth(entity);
		makePlayerDropItems(entity, damageSource);
		makePlayerDropShoulderEntities((PlayerEntity) player);

		if (world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)){
			forgivePlayerMobAnger(player);
		}

		player.getScoreboard().forEachScore(ScoreboardCriterion.DEATH_COUNT, player.getEntityName(), ScoreboardPlayerScore::incrementScore);

		LivingEntity livingEntity = entity.getPrimeAdversary();
		if(livingEntity != null){
			incrementKilledByStat(player, damageSource, livingEntity);
		}

		world.sendEntityStatus(player, (byte) 3);
		player.incrementStat(Stats.DEATHS);
		player.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_DEATH));
		player.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
		player.extinguish();
		player.setFrozenTicks(0);
		player.setOnFire(false);
		player.getDamageTracker().update();
		player.setLastDeathPos(Optional.of(GlobalPos.create(world.getRegistryKey(), player.getBlockPos())));

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

	private static void incrementKilledByStat(ServerPlayerEntity entity, DamageSource damageSource, LivingEntity livingEntity) {
		try {
			Method onKilledBy = LivingEntity.class.getDeclaredMethod("onKilledBy", LivingEntity.class);
			onKilledBy.setAccessible(true);
			Field scoreAmount = LivingEntity.class.getDeclaredField("scoreAmount");
			entity.incrementStat(Stats.KILLED_BY.getOrCreateStat(livingEntity.getType()));
			livingEntity.updateKilledAdvancementCriterion(entity, scoreAmount.getInt(entity), damageSource);
			onKilledBy.invoke(entity, livingEntity);
		} catch (Exception e){
			LOGGER.error(e.toString());
		}
	}

	private static void forgivePlayerMobAnger(ServerPlayerEntity entity) {
		try {
			Method forgiveMobAnger = ServerPlayerEntity.class.getDeclaredMethod("forgiveMobAnger");
			forgiveMobAnger.setAccessible(true);
			forgiveMobAnger.invoke(entity);
		} catch (Exception e){
			LOGGER.error(e.toString());
		}
	}

	private static void makePlayerDropShoulderEntities(PlayerEntity entity) {
		try {
			Method shoulderEntities = PlayerEntity.class.getDeclaredMethod("dropShoulderEntities");
			shoulderEntities.setAccessible(true);
			shoulderEntities.invoke(entity);
		} catch (Exception e){
			LOGGER.error(e.toString());
		}
	}

	private static void sendDeathMessage(ServerPlayerEntity player, ServerWorld world, MinecraftServer server){
		player.emitGameEvent(GameEvent.ENTITY_DIE);
		boolean bl = world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES);
		if (bl) {
			Text text = player.getDamageTracker().getDeathMessage();

			AbstractTeam abstractTeam = player.getScoreboardTeam();
			if (abstractTeam != null && abstractTeam.getDeathMessageVisibilityRule() != AbstractTeam.VisibilityRule.ALWAYS) {
				if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS) {
					server.getPlayerManager().sendToTeam(player, text);
				} else if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM) {
					server.getPlayerManager().sendToOtherTeams(player, text);
				}
			} else {
				server.getPlayerManager().broadcast(text, false);
			}
		} else {
			player.networkHandler.sendPacket(new DeathMessageS2CPacket(player.getId(), ScreenTexts.EMPTY));
		}
	}

	private static void resetPlayerHealth(LivingEntity entity) {
		entity.setHealth(20.0f);
		ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.RESURRECT_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			if(player.currentScreenHandler instanceof RitualTableScreenHandler){
				RitualTableScreenHandler ritualTableScreenHandler = (RitualTableScreenHandler) player.currentScreenHandler;
				ritualTableScreenHandler.tryRevive(buf.readUuid(), player);

			}
		});

	}
}