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
    public static final int LIGHT_GRAY_COLOR = ColorHelper.Argb.getArgb((int)140, (int)255, (int)255, (int)255);
    public static final int WHITE_COLOR = ColorHelper.Argb.getArgb((int)255, (int)255, (int)255, (int)255);
    public static final int LIGHT_BLUE_COLOR = ColorHelper.Argb.getArgb((int)140, (int)136, (int)146, (int)201);
    private boolean hovered;

    private int y;
    private int x;
    private int width;
    private int height;
    private int yOffset = 0;

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

    public int getY(){
        return this.y;
    }

    public int getX(){
        return this.x;
    }

    public void scrollY(double scrollAmount){
        this.yOffset = (int) scrollAmount;
    }

    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        this.y = y - this.yOffset;
        this.x = x;
        this.width = entryWidth;
        this.height = entryHeight;

        int l; // Text y value
        int i = x + 4; // Head x value
        int j = this.y + (entryHeight - 12) / 2; // Head y value
        int k = i + 12 + 4; // Text x Value

        this.hovered = mouseX >= x && mouseY >= this.y && mouseX < x + entryWidth && mouseY < this.y + entryHeight;
        if (this.parent.getSelected() == this) {
            context.fill(x, this.y, x + entryWidth, this.y + entryHeight, LIGHT_BLUE_COLOR);
        } else if(this.hovered) {
            context.fill(x, this.y, x + entryWidth, this.y + entryHeight, LIGHT_GRAY_COLOR);
        } else {
            context.fill(x, this.y, x + entryWidth, this.y + entryHeight, GRAY_COLOR);
        }
        l = this.y + (entryHeight - this.client.textRenderer.fontHeight) / 2;
        PlayerSkinDrawer.draw(context, this.skinSupplier.get(), i, j, 12);
        context.drawText(this.client.textRenderer, this.name, k, l, WHITE_COLOR, false);
    }
}
