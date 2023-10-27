package lassevkp.revivals;

import lassevkp.revivals.screen.ModScreenHandlers;
import lassevkp.revivals.screen.RitualTableScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.text.Text;

import java.awt.event.ActionListener;
import java.util.EventListener;
import java.util.EventListenerProxy;
import java.util.Observer;
import java.util.UUID;

public class RevivalsClient implements ClientModInitializer {
	//public static Event clientPlayerDiedEventObserver = EventFactory.createArrayBacked(void.class, callbacks -> {

	//});

	@Override
	public void onInitializeClient() {
		HandledScreens.register(ModScreenHandlers.RITUAL_TABLE_SCREEN_HANDLER, RitualTableScreen::new);


	}
}