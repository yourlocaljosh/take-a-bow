package com.klism;

import com.klism.item.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Takeabow implements ModInitializer{
	public static final String MOD_ID = "takeabow";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		//debugger
		LOGGER.info("Initializing Take A Bow mod");
		ModItems.registerModItems();
	}
}