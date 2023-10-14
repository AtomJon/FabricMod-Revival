package lassevkp.revivals;

import lassevkp.revivals.block.ModBlocks;
import lassevkp.revivals.block.entity.ModBlockEntities;
import lassevkp.revivals.item.ModItems;
import lassevkp.revivals.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Revivals implements ModInitializer {

	public static final String MOD_ID = "revivals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerAllScreenHandlers();

		LOGGER.info("Revivals is initialized! =D");
	}
}