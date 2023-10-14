package lassevkp.revivals;

import lassevkp.revivals.screen.ModScreenHandlers;
import lassevkp.revivals.screen.RitualTableScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class RevivalsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(ModScreenHandlers.RITUAL_TABLE_SCREEN_HANDLER, RitualTableScreen::new);
	}
}