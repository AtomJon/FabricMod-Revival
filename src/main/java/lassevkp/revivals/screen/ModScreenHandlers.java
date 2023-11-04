package lassevkp.revivals.screen;

import lassevkp.revivals.Revivals;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static ScreenHandlerType<RitualTableScreenHandler> RITUAL_TABLE_SCREEN_HANDLER;

    public static void registerAllScreenHandlers(){
        RITUAL_TABLE_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(Revivals.MOD_ID, "ritual_table"), RitualTableScreenHandler::new);

    }
}
