package lassevkp.revivals;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.core.config.plugins.convert.TypeConverters;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StateSaverAndLoader extends PersistentState {

    private static final String deadPlayersNbtKey = "deadPlayers";
    public List<UUID> deadPlayers = new ArrayList<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putByteArray(deadPlayersNbtKey, SerializationUtils.serialize(deadPlayers.toArray()));
        return nbt;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag) {
        byte[] data = tag.getByteArray(deadPlayersNbtKey);

        UUID[] deadPlayers = SerializationUtils.deserialize(data);

        StateSaverAndLoader state = new StateSaverAndLoader();
        state.deadPlayers = List.of(deadPlayers);
        return state;
    }

    private static Type<StateSaverAndLoader> type = new Type<>(
            StateSaverAndLoader::new, // If there's no 'StateSaverAndLoader' yet create one
            StateSaverAndLoader::createFromNbt, // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        // The first time the following 'getOrCreate' function is called, it creates a brand new 'StateSaverAndLoader' and
        // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
        // 'StateSaverAndLoader' NBT on disk to our function 'StateSaverAndLoader::createFromNbt'.
        StateSaverAndLoader state = persistentStateManager.getOrCreate(type, Revivals.MOD_ID);

        // If state is not marked dirty, when Minecraft closes, 'writeNbt' won't be called and therefore nothing will be saved.
        // Technically it's 'cleaner' if you only mark state as dirty when there was actually a change, but the vast majority
        // of mod writers are just going to be confused when their data isn't being saved, and so it's best just to 'markDirty' for them.
        // Besides, it's literally just setting a bool to true, and the only time there's a 'cost' is when the file is written to disk when
        // there were no actual change to any of the mods state (INCREDIBLY RARE).
        state.markDirty();

        return state;
    }
}