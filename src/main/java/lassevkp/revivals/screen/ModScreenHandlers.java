package lassevkp.revivals.screen;

import lassevkp.revivals.Revivals;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static ScreenHandlerType<RitualTableScreenHandler> RITUAL_TABLE_SCREEN_HANDLER;

    public static void registerAllScreenHandlers(){
        //RITUAL_TABLE_SCREEN_HANDLER = new ScreenHandlerType<>(RitualTableScreenHandler::new, FeatureSet.empty());
        RITUAL_TABLE_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(Revivals.MOD_ID, "ritual_table"), RitualTableScreenHandler::new);

    }
}
