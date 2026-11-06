package dev.btstudio.modmenufix;

import com.terraformersmc.modmenu.config.ModMenuConfig.GameMenuButtonStyle;
import com.terraformersmc.modmenu.config.ModMenuConfig.TitleMenuButtonStyle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MenuButtonsTest {
	@Test
	void acceptsEveryTitleStyle() {
		assertEquals(TitleMenuButtonStyle.CLASSIC, MenuButtons.parseTitleStyle("classic"));
		assertEquals(TitleMenuButtonStyle.ICON, MenuButtons.parseTitleStyle("icon"));
		assertEquals(TitleMenuButtonStyle.SHRINK, MenuButtons.parseTitleStyle("shrink"));
		assertEquals(TitleMenuButtonStyle.REPLACE_REALMS, MenuButtons.parseTitleStyle("replace_realms"));
	}

	@Test
	void acceptsEveryPauseMenuStyleIncludingModMenuAliases() {
		assertEquals(GameMenuButtonStyle.INSERT, MenuButtons.parseGameMenuStyle("insert"));
		assertEquals(GameMenuButtonStyle.INSERT, MenuButtons.parseGameMenuStyle("below_bugs"));
		assertEquals(GameMenuButtonStyle.ICON, MenuButtons.parseGameMenuStyle("icon"));
		assertEquals(GameMenuButtonStyle.ICON, MenuButtons.parseGameMenuStyle("replace"));
		assertEquals(GameMenuButtonStyle.ICON, MenuButtons.parseGameMenuStyle("replace_bugs"));
	}

	@Test
	void ignoresCaseAndSurroundingSpace() {
		assertEquals(TitleMenuButtonStyle.CLASSIC, MenuButtons.parseTitleStyle("  Classic "));
		assertEquals(GameMenuButtonStyle.INSERT, MenuButtons.parseGameMenuStyle("INSERT"));
	}

	@Test
	void keepAndUnknownValuesLeaveModMenuAlone() {
		assertNull(MenuButtons.parseTitleStyle("keep"));
		assertNull(MenuButtons.parseTitleStyle(""));
		assertNull(MenuButtons.parseTitleStyle("classic-button"));
		assertNull(MenuButtons.parseTitleStyle(null));
		assertNull(MenuButtons.parseGameMenuStyle("keep"));
		assertNull(MenuButtons.parseGameMenuStyle("below"));
		assertNull(MenuButtons.parseGameMenuStyle(null));
	}
}
