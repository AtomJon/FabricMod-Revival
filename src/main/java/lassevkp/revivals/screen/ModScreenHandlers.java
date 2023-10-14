package lassevkp.revivals.screen;

import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlers {
    public static ScreenHandlerType<RitualTableScreenHandler> RITUAL_TABLE_SCREEN_HANDLER;

    public static void registerAllScreenHandlers(){
        RITUAL_TABLE_SCREEN_HANDLER = new ScreenHandlerType<>(RitualTableScreenHandler::new, FeatureSet.empty());
    }
}
