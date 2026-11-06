package dev.btstudio.modmenufix;

import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.config.ModMenuConfig.GameMenuButtonStyle;
import com.terraformersmc.modmenu.config.ModMenuConfig.TitleMenuButtonStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Puts the "Mods" GUI entry where the player expects it, on the title screen and inside the
 * pause menu.
 *
 * <p>Mod Menu 21 knows several layouts for both places. Two of them matter here:</p>
 *
 * <ul>
 *     <li>title screen: {@code classic} — the full-width "Mods" button in the menu column
 *     under Minecraft Realms (the 26.2 look) versus {@code icon} — the 20×20 icon that
 *     Minecraft 26.3 moved into its icon row, which is Mod Menu's default;</li>
 *     <li>pause menu: {@code insert} — a full-width "Mods" button in the game menu grid
 *     versus {@code icon} — a 20×20 icon in the pause menu's icon row (the default).</li>
 * </ul>
 *
 * <p>Nothing here reimplements a layout: it selects the style inside Mod Menu's own config
 * after Mod Menu has loaded it and before any screen is built, so Mod Menu's own code does
 * the placement.</p>
 */
public final class MenuButtons {
	private static final Logger LOGGER = LoggerFactory.getLogger("Mod Menu Fix/Menu Buttons");

	private MenuButtons() {
	}

	/** Parses {@code title_screen_button}; {@code null} for {@code keep} or an unknown value. */
	static TitleMenuButtonStyle parseTitleStyle(String value) {
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

	/** Parses {@code game_menu_button}; {@code null} for {@code keep} or an unknown value. */
	static GameMenuButtonStyle parseGameMenuStyle(String value) {
		if (value == null) {
			return null;
		}

		return switch (value.trim().toLowerCase(Locale.ROOT)) {
			case "insert", "below_bugs" -> GameMenuButtonStyle.INSERT;
			case "icon", "replace", "replace_bugs" -> GameMenuButtonStyle.ICON;
			default -> null;
		};
	}

	public static void apply(FixConfig config) {
		applyTitleScreen(config.titleScreenButton);
		applyGameMenu(config.gameMenuButton);
	}

	private static void applyTitleScreen(String requested) {
		TitleMenuButtonStyle style = parseTitleStyle(requested);

		if (style == null) {
			warnUnknown("title_screen_button", requested, "classic, icon, shrink, replace_realms, keep");
			return;
		}

		if (!ModMenuConfig.MODIFY_TITLE_SCREEN.getValue()) {
			// Without this Mod Menu never adds its button at all.
			ModMenuConfig.MODIFY_TITLE_SCREEN.setValue(true);
			LOGGER.info("Enabled Mod Menu's title screen button because title_screen_button is set");
		}

		TitleMenuButtonStyle current = ModMenuConfig.MODS_BUTTON_STYLE.getValue();

		if (current == style) {
			LOGGER.info("Title screen mods button style is already '{}'", name(style));
			return;
		}

		ModMenuConfig.MODS_BUTTON_STYLE.setValue(style);
		LOGGER.info(
				"Title screen mods button style: '{}' -> '{}' (change title_screen_button in modmenufix.json)",
				name(current),
				name(style)
		);
	}

	private static void applyGameMenu(String requested) {
		GameMenuButtonStyle style = parseGameMenuStyle(requested);

		if (style == null) {
			warnUnknown("game_menu_button", requested, "insert, icon, keep");
			return;
		}

		if (!ModMenuConfig.MODIFY_GAME_MENU.getValue()) {
			ModMenuConfig.MODIFY_GAME_MENU.setValue(true);
			LOGGER.info("Enabled Mod Menu's pause menu button because game_menu_button is set");
		}

		GameMenuButtonStyle current = ModMenuConfig.GAME_MENU_BUTTON_STYLE.getValue();

		if (current == style) {
			LOGGER.info("Pause menu mods button style is already '{}'", name(style));
			return;
		}

		ModMenuConfig.GAME_MENU_BUTTON_STYLE.setValue(style);
		LOGGER.info(
				"Pause menu mods button style: '{}' -> '{}' (change game_menu_button in modmenufix.json)",
				name(current),
				name(style)
		);
	}

	private static void warnUnknown(String key, String requested, String expected) {
		if (requested == null || requested.isBlank() || requested.equalsIgnoreCase("keep")) {
			return;
		}

		LOGGER.warn(
				"Unknown {} value '{}' in modmenufix.json (expected {}), leaving Mod Menu's setting alone",
				key,
				requested,
				expected
		);
	}

	private static String name(Enum<?> style) {
		return style == null ? "unknown" : style.name().toLowerCase(Locale.ROOT);
	}
}
