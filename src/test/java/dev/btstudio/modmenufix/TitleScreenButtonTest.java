package dev.btstudio.modmenufix;

import com.terraformersmc.modmenu.config.ModMenuConfig.TitleMenuButtonStyle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TitleScreenButtonTest {
	@Test
	void acceptsEveryStyleName() {
		assertEquals(TitleMenuButtonStyle.CLASSIC, TitleScreenButton.parse("classic"));
		assertEquals(TitleMenuButtonStyle.ICON, TitleScreenButton.parse("icon"));
		assertEquals(TitleMenuButtonStyle.SHRINK, TitleScreenButton.parse("shrink"));
		assertEquals(TitleMenuButtonStyle.REPLACE_REALMS, TitleScreenButton.parse("replace_realms"));
	}

	@Test
	void ignoresCaseAndSurroundingSpace() {
		assertEquals(TitleMenuButtonStyle.CLASSIC, TitleScreenButton.parse("  Classic "));
		assertEquals(TitleMenuButtonStyle.ICON, TitleScreenButton.parse("ICON"));
	}

	@Test
	void keepAndUnknownValuesLeaveModMenuAlone() {
		assertNull(TitleScreenButton.parse("keep"));
		assertNull(TitleScreenButton.parse(""));
		assertNull(TitleScreenButton.parse("classic-button"));
		assertNull(TitleScreenButton.parse(null));
	}
}
