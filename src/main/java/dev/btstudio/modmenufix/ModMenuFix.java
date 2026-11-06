package dev.btstudio.modmenufix;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ModMenuFix implements ClientModInitializer {
	public static final String MOD_ID = "modmenufix";
	public static final Logger LOGGER = LoggerFactory.getLogger("Mod Menu Fix");

	@Override
	public void onInitializeClient() {
		FixConfig config = FixConfig.get();

		LOGGER.info(
				"Mod Menu Fix 1.0.0 for Minecraft 26.3 ready (hide missing parents: {}, guard update checker: {})",
				config.hideMissingParentMods,
				config.guardUpdateCheckerAgainstMissingParents
		);
	}
}
