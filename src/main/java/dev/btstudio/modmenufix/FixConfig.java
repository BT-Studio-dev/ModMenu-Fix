package dev.btstudio.modmenufix;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Plain JSON configuration, stored as {@code config/modmenufix.json}.
 *
 * <p>Every option is read lazily, so a broken or hand-edited file can never stop
 * Mod Menu from starting: on any error the defaults below are used.</p>
 */
public final class FixConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger("Mod Menu Fix/Config");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String FILE_NAME = "modmenufix.json";

	private static FixConfig instance;

	/**
	 * Remove parent entries that Mod Menu invented for a mod that is not installed.
	 * This is the actual fix; set to {@code false} to get stock Mod Menu behaviour back.
	 */
	@SerializedName("hide_missing_parent_mods")
	public boolean hideMissingParentMods = true;

	/**
	 * When a missing parent is removed, its installed children are kept visible and are
	 * counted as top-level mods. Disable to keep them out of the "N mods" counters.
	 */
	@SerializedName("count_orphaned_children_as_root_mods")
	public boolean countOrphanedChildrenAsRootMods = true;

	/**
	 * Log one line per missing parent (mod id plus the installed mods that reference it).
	 */
	@SerializedName("log_missing_parents")
	public boolean logMissingParents = true;

	/**
	 * Which title screen Mods entry to use: {@code classic} (full-width "Mods" button under
	 * Minecraft Realms, the 26.2 look), {@code icon} (20x20 icon inside the 26.3 icon row),
	 * {@code shrink} (half-width next to Realms), {@code replace_realms}, or {@code keep}
	 * (do not touch Mod Menu's own {@code mods_button_style} setting).
	 */
	@SerializedName("title_screen_button")
	public String titleScreenButton = "classic";

	/**
	 * Guard the update checker against {@code ModMenu.MODS.get(parent)} returning null.
	 * Keep this on unless you want the original crash back.
	 */
	@SerializedName("guard_update_checker_against_missing_parents")
	public boolean guardUpdateCheckerAgainstMissingParents = true;

	public static FixConfig get() {
		FixConfig config = instance;
		if (config == null) {
			synchronized (FixConfig.class) {
				config = instance;
				if (config == null) {
					config = load();
					instance = config;
				}
			}
		}

		return config;
	}

	private static FixConfig load() {
		Path path = path();

		try {
			if (Files.notExists(path)) {
				FixConfig defaults = new FixConfig();
				defaults.save(path);
				return defaults;
			}

			String json = Files.readString(path, StandardCharsets.UTF_8);
			FixConfig config = GSON.fromJson(json, FixConfig.class);

			if (config == null) {
				LOGGER.warn("{} is empty, using defaults", path);
				return new FixConfig();
			}

			return config;
		} catch (Exception e) {
			LOGGER.warn("Could not read {}, using defaults", path, e);
			return new FixConfig();
		}
	}

	private void save(Path path) {
		try {
			Files.createDirectories(path.getParent());
			Files.writeString(path, GSON.toJson(this) + System.lineSeparator(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			LOGGER.warn("Could not write default config to {}", path, e);
		}
	}

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
	}
}
