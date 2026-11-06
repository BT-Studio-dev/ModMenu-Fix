package dev.btstudio.modmenufix;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ModMenuFix implements ClientModInitializer {
	public static final String MOD_ID = "modmenufix";
	public static final Logger LOGGER = LoggerFactory.getLogger("Mod Menu Fix");

	@Override
	public void onInitializeClient() {
		FixConfig config = FixConfig.get();
		String version = FabricLoader.getInstance()
				.getModContainer(MOD_ID)
				.map(container -> container.getMetadata().getVersion().getFriendlyString())
				.orElse("unknown");

		LOGGER.info(
				"Mod Menu Fix {} for Minecraft 26.3 ready (hide missing parents: {}, title screen button: {}, guard update checker: {})",
				version,
				config.hideMissingParentMods,
				config.titleScreenButton,
				config.guardUpdateCheckerAgainstMissingParents
		);
	}
}
