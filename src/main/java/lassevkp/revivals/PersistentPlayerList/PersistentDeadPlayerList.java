package lassevkp.revivals.PersistentPlayerList;

import lassevkp.revivals.Revivals;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.apache.commons.lang3.SerializationUtils;

import java.util.*;

public class PersistentDeadPlayerList extends PersistentState {
    private static final String DeadPlayersNbtKey = "deadPlayers";

    protected HashSet<UUID> deadPlayers;

    public PersistentDeadPlayerList() {
        deadPlayers = new HashSet<>();
    }

    public PersistentDeadPlayerList(Collection<UUID> players) {
        deadPlayers = new HashSet<>(players);
    }

    public Collection<UUID> getDeadPlayers() { return deadPlayers; }

    public boolean isPlayerUUIDDead(UUID player)
    {
        return deadPlayers.contains(player);
    }

    public void setPlayerIsDead(UUID player)
    {
        this.markDirty();
        deadPlayers.add(player);
    }

    public void setPlayerNotDead(UUID player)
    {
        this.markDirty();
        deadPlayers.remove(player);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        this.markDirty();

        byte[] data = SerializationUtils.serialize(deadPlayers);
        nbt.putByteArray(DeadPlayersNbtKey, data);

        return nbt;
    }

    public static PersistentDeadPlayerList createFromNbt(NbtCompound tag) {
        byte[] data = tag.getByteArray(DeadPlayersNbtKey);

        Collection<UUID> collectionDeadPlayers = SerializationUtils.deserialize(data);

        PersistentDeadPlayerList state = new PersistentDeadPlayerList(collectionDeadPlayers);
        return state;
    }

    private static final Type<PersistentDeadPlayerList> type = new Type<>(
            PersistentDeadPlayerList::new, // If there's no 'StateSaverAndLoader' yet create one
            PersistentDeadPlayerList::createFromNbt, // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );

    public static PersistentDeadPlayerList getServerDeadPlayerList(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        if (persistentStateManager == null) {
            Revivals.LOGGER.error("Could not fetch PersistentStateManager for world.");
            throw new AssertionError();
        }

        PersistentDeadPlayerList state = persistentStateManager.getOrCreate(type, Revivals.MOD_ID);

        return state;
    }
}
