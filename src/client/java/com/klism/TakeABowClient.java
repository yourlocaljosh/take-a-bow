package com.klism;

import net.fabricmc.api.ClientModInitializer;

public class TakeabowClient implements ClientModInitializer{
	@Override
	public void onInitializeClient(){
		Takeabow.LOGGER.info("Client initialization for Take A Bow");
	}
}