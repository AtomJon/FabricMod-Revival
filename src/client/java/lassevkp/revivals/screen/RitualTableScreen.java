package lassevkp.revivals.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import lassevkp.revivals.Revivals;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class RitualTableScreen extends HandledScreen<RitualTableScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(Revivals.MOD_ID, "textures/gui/ritual_table_gui.png");
    private RitualTablePlayerListEntry selected;
    RitualTablePlayerListWidget playerList;

    ResurrectButtonWidget resurrectButton;

    public RitualTableScreen(RitualTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    private int getScreenHeight() {
        return Math.max(52, this.height - 128 - 16);
    }

    private int getCenterX(){
        return this.width/2;
    }

    private int getCenterY(){
        return this.height/2;
    }

    private int getMargin(int margin, int anchor, int height){
        return anchor + (anchor * margin / (height/2));
    }


    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        this.playerList = new RitualTablePlayerListWidget(this, this.client, this.width, this.height, 41, getMargin(-7, getCenterY(), this.height), 24);
        this.resurrectButton = new ResurrectButtonWidget(getMargin(2, getCenterX(), this.height), getMargin(-30, getCenterY(), this.height),75,16,
                new ButtonTextures(Identifier.of("minecraft", "widget/button"),
                        Identifier.of("minecraft", "widget/button_disabled"),
                        Identifier.of("minecraft", "widget/button_highlighted")),
                button -> {
            this.revive();
                }, client);
        addSelectableChild(this.resurrectButton);
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
        this.resurrectButton.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            RitualTablePlayerListEntry hovered = playerList.getHoveredEntry((int) mouseX, (int) mouseY);
            if(hovered != null){
                this.selected = hovered;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public RitualTablePlayerListEntry getSelected() {
        return selected;
    }

    private void revive(){
        if(selected != null){
            System.out.println("Reviving");
            UUID reviveeUUID = selected.getUuid();
            UUID tableUserUUID = client.player.getUuid();

            // Send revive information (Also check if player is already alive and online)
            // tableUserUUID is used for when making a message of who revived reviveeUUID
        }
    }


}
