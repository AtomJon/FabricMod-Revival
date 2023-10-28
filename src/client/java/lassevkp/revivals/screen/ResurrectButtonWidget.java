package lassevkp.revivals.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

public class ResurrectButtonWidget extends TexturedButtonWidget {
    private static final Identifier ICON = new Identifier("revivals", "container/ritual_table/resurrect_icon");
    public static final int WHITE_COLOR = ColorHelper.Argb.getArgb((int)255, (int)255, (int)255, (int)255);
    private final MinecraftClient client;

    public ResurrectButtonWidget(int x, int y, int width, int height, ButtonTextures textures, PressAction pressAction, MinecraftClient client) {
        super(x, y, width, height, textures, pressAction);
        this.client = client;
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderButton(context, mouseX, mouseY, delta);

        int iconX = getX() + 3;
        int iconY = getY() + 3;
        int iconWidth = 10;
        int iconHeight = 10;
        int textX = iconX + iconWidth + 4;
        int textY = iconY + 1;

        context.drawGuiTexture(ICON, iconX, iconY, iconWidth, iconHeight);
        context.drawText(this.client.textRenderer, Text.translatable("gui.ritual_table.resurrect_button"), textX, textY, WHITE_COLOR, true);
    }
}
