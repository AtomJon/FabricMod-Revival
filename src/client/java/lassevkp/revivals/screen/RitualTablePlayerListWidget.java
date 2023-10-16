package lassevkp.revivals.screen;

import com.google.common.collect.Lists;
import lassevkp.revivals.Revivals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;

import java.util.*;

public class RitualTablePlayerListWidget extends ElementListWidget<RitualTablePlayerListEntry> {
    private final RitualTableScreen parent;
    private final List<RitualTablePlayerListEntry> players = Lists.newArrayList();

    public RitualTablePlayerListWidget(RitualTableScreen parent, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        this.parent = parent;
        this.setRenderBackground(false);

        Collection<UUID> collection = this.client.player.networkHandler.getPlayerUuids();
        this.update(collection, 0.0);
    }

    public void update(Collection<UUID> uuids, double scrollAmount){ // Doesn't update
        Revivals.LOGGER.info("updating");
        HashMap<UUID, RitualTablePlayerListEntry> map = new HashMap<>();
        this.setPlayers(uuids, map);
        this.refresh(map.values(), scrollAmount);

    }

    private void setPlayers(Collection<UUID> playerUuids, Map<UUID, RitualTablePlayerListEntry> entriesByUuids){
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
        for (UUID uUID : playerUuids) {
            PlayerListEntry playerListEntry = clientPlayNetworkHandler.getPlayerListEntry(uUID);
            if (playerListEntry == null) continue;
            entriesByUuids.put(uUID, new RitualTablePlayerListEntry(this.client, this.parent, uUID, playerListEntry.getProfile().getName(), playerListEntry::getSkinTextures));
        }
    }

    private void sortPlayers() {
        this.players.sort(Comparator.comparing((RitualTablePlayerListEntry player) -> {
            int i;
            if (!player.getName().isBlank() && ((i = player.getName().codePointAt(0)) == 95 || i >= 97 && i <= 122 || i >= 65 && i <= 90 || i >= 48 && i <= 57)) {
                return 0;
            }
            return 1;
        }).thenComparing(RitualTablePlayerListEntry::getName, String::compareToIgnoreCase));
    }

    private void filterPlayers() {
        // Remove alive players
    }

    private void refresh(Collection<RitualTablePlayerListEntry> players, double scrollAmount) {
        this.players.clear();
        this.players.addAll(players);
        this.sortPlayers();
        this.filterPlayers();
        this.replaceEntries(this.players);
        this.setScrollAmount(scrollAmount);
    }

    public boolean isEmpty() {
        return this.players.isEmpty();
    }

    @Override
    protected int getRowTop(int index) {
        return this.height/2-75+index*22;
    }

    @Override
    public int getRowLeft() {
        return this.left + this.width / 2 - this.getRowWidth()-5;
    }

    @Override
    public int getRowWidth() {
        return 75;
    }

    @Override
    protected int getScrollbarPositionX() {
        return this.width / 2 - 96;
    }

    @Override
    protected int getRowBottom(int index) {
        return super.getRowBottom(index);
    }
}