package lassevkp.revivals.screen;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.PlayerSkinTexture;

import java.util.*;

public class RitualTablePlayerListWidget extends ElementListWidget<RitualTablePlayerListEntry> {
    private final RitualTableScreen parent;
    private final List<RitualTablePlayerListEntry> players = Lists.newArrayList();
    private Collection<UUID> deadPlayers;

    public RitualTablePlayerListWidget(RitualTableScreen parent, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        this.parent = parent;
        this.setRenderBackground(false);

        this.deadPlayers = parent.getScreenHandler().getDeadPlayers();

        this.setRenderHeader(false, 0);

        this.update(this.deadPlayers, 0.0);
    }

    public void update(Collection<UUID> uuids, double scrollAmount){
        HashMap<UUID, RitualTablePlayerListEntry> map = new HashMap<>();
        this.setPlayers(uuids, map);
        this.refresh(map.values(), scrollAmount);

    }

    private void setPlayers(Collection<UUID> playerUuids, Map<UUID, RitualTablePlayerListEntry> entriesByUuids){
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
        for (UUID uUID : playerUuids) {
            PlayerListEntry playerListEntry = clientPlayNetworkHandler.getPlayerListEntry(uUID);
            if (playerListEntry == null) continue;
            entriesByUuids.put(uUID, new RitualTablePlayerListEntry(this.client, this.parent, uUID, playerListEntry.getProfile().getName(), playerListEntry::getSkinTexture));
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

    private void refresh(Collection<RitualTablePlayerListEntry> players, double scrollAmount) {
        this.players.clear();
        this.players.addAll(players);
        this.sortPlayers();
        if(this.children().isEmpty()){
            this.replaceEntries(this.players);
        }
        for (RitualTablePlayerListEntry entry: this.children()){
            entry.scrollY(scrollAmount);
        }
    }

    public boolean isEmpty() {
        return this.players.isEmpty();
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

    protected RitualTablePlayerListEntry getHoveredEntry(int mouseX, int mouseY){

        for (RitualTablePlayerListEntry entry: this.children()) {
            int entryX = entry.getX();
            int entryY = entry.getY();
            int entryWidth = entry.getWidth();
            int entryHeight = entry.getHeight();
            if(mouseX >= entryX && mouseY >= entryY && mouseX < entryX + entryWidth && mouseY < entryY + entryHeight){
                return entry;
            }
        }
        return null;
    }


    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if(mouseY < this.bottom && mouseY >= this.top && mouseX >= this.getRowLeft() && mouseX < this.getRowRight()) {
            this.setScrollAmount(this.getScrollAmount()-verticalAmount*4);
            this.update(this.deadPlayers, this.getScrollAmount());
        }
        return false;
    }

    @Override
    protected void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
        int i = this.getRowLeft();
        int j = this.getRowWidth();
        int k = this.itemHeight - 4;
        int l = this.getEntryCount();

        for(int m = 0; m < l; ++m) {
            int n = this.getRowTop(m);
            int o = this.getRowBottom(m);
            if (o >= this.top && n <= this.bottom) {
                this.renderEntry(context, mouseX, mouseY, delta, m, i, n, j, k);
            }
        }
    }
}
