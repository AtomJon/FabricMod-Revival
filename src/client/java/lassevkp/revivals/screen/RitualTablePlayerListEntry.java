package lassevkp.revivals.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.math.ColorHelper;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class RitualTablePlayerListEntry extends ElementListWidget.Entry<RitualTablePlayerListEntry> {
    private final MinecraftClient client;
    private final UUID uuid;
    private final String name;
    private final RitualTableScreen parent;
    private final Supplier<SkinTextures> skinSupplier;
    public static final int GRAY_COLOR = ColorHelper.Argb.getArgb((int)255, (int)74, (int)74, (int)74);
    public static final int WHITE_COLOR = ColorHelper.Argb.getArgb((int)255, (int)255, (int)255, (int)255);

    public RitualTablePlayerListEntry(MinecraftClient client, RitualTableScreen parent, UUID uuid, String name, Supplier<SkinTextures> skinTexture){
        this.client = client;
        this.uuid = uuid;
        this.name = name;
        this.parent = parent;
        this.skinSupplier = skinTexture;
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return null;
    }

    @Override
    public List<? extends Element> children() {
        return null;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Supplier<SkinTextures> getSkinSupplier() {
        return this.skinSupplier;
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int l; // Text y value
        int i = x + 4;
        int j = y + (entryHeight - 12) / 2; // head height
        int k = i + 12 + 4; // Text x Value
        context.fill(x, y, x + entryWidth, y + entryHeight, GRAY_COLOR);
        l = y + (entryHeight - this.client.textRenderer.fontHeight) / 2;
        PlayerSkinDrawer.draw(context, this.skinSupplier.get(), i, j, 12);
        context.drawText(this.client.textRenderer, this.name, k, l, WHITE_COLOR, false);
    }



}
