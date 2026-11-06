package dev.btstudio.modmenufix;

import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.config.ModMenuConfig.TitleMenuButtonStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Puts the "Mods" entry on the title screen where the player expects it.
 *
 * <p>Mod Menu 21 knows four layouts ({@code mods_button_style}): the classic full-width
 * "Mods" button under Minecraft Realms, a button replacing the Realms row, a shrunken
 * side-by-side button, and the 26.3 default of a 20×20 icon inside the title screen's icon
 * row. Minecraft 26.3 moved that icon row, so people who are used to the 26.2 look end up
 * hunting for a small icon instead of a labelled button.</p>
 *
 * <p>Instead of reimplementing the layout (and fighting vanilla's button positions), this
 * simply selects the style inside Mod Menu's own config before the title screen is built,
 * so Mod Menu's well-tested code does the placement.</p>
 */
public final class TitleScreenButton {
	private static final Logger LOGGER = LoggerFactory.getLogger("Mod Menu Fix/Title Screen");

	private TitleScreenButton() {
	}

	/** Parses the config value; {@code null} when the value is {@code keep} or unknown. */
	static TitleMenuButtonStyle parse(String value) {
		if (value == null) {
			return null;
		}

		return switch (value.trim().toLowerCase(Locale.ROOT)) {
			case "classic" -> TitleMenuButtonStyle.CLASSIC;
			case "icon" -> TitleMenuButtonStyle.ICON;
			case "shrink" -> TitleMenuButtonStyle.SHRINK;
			case "replace_realms" -> TitleMenuButtonStyle.REPLACE_REALMS;
			default -> null;
		};
	}

	public static void apply(FixConfig config) {
		String requested = config.titleScreenButton;
		TitleMenuButtonStyle style = parse(requested);

		if (style == null) {
			if (requested != null && !requested.equalsIgnoreCase("keep")) {
				LOGGER.warn(
						"Unknown title_screen_button value '{}' in modmenufix.json (expected classic, icon, shrink, replace_realms or keep), leaving Mod Menu's setting alone",
						requested
				);
			}

			return;
		}

		if (!ModMenuConfig.MODIFY_TITLE_SCREEN.getValue()) {
			// Without this Mod Menu never adds its button at all.
			ModMenuConfig.MODIFY_TITLE_SCREEN.setValue(true);
			LOGGER.info("Enabled Mod Menu's title screen button because title_screen_button is set");
		}

		TitleMenuButtonStyle current = ModMenuConfig.MODS_BUTTON_STYLE.getValue();
		if (current == style) {
			LOGGER.info("Title screen mods button style is already '{}'", style.name().toLowerCase(Locale.ROOT));
			return;
		}

		ModMenuConfig.MODS_BUTTON_STYLE.setValue(style);
		LOGGER.info(
				"Title screen mods button style: '{}' -> '{}' (set title_screen_button in modmenufix.json to change it)",
				current == null ? "unknown" : current.name().toLowerCase(Locale.ROOT),
				style.name().toLowerCase(Locale.ROOT)
		);
	}
}
