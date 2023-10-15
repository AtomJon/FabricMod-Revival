package lassevkp.revivals.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import lassevkp.revivals.Revivals;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class RitualTableScreen extends HandledScreen<RitualTableScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(Revivals.MOD_ID, "textures/gui/ritual_table_gui.png");
    RitualTablePlayerListWidget playerList;

    public RitualTableScreen(RitualTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    private int getScreenHeight() {
        return Math.max(52, this.height - 128 - 16);
    }

    private int getPlayerListBottom() {
        return 40 + this.getScreenHeight() - 8;

    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        System.out.println(350-this.height/2); // The cutoff for players are bad if you resize the windows
        this.playerList = new RitualTablePlayerListWidget(this, this.client, this.width, this.height, 41, this.getPlayerListBottom()+105-this.height/2, 24);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        if (!this.playerList.isEmpty()) {
            this.playerList.render(context, mouseX, mouseY, delta);
        }
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

}
