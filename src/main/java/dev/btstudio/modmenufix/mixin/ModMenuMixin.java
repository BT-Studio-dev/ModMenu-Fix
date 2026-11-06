package dev.btstudio.modmenufix.mixin;

import com.terraformersmc.modmenu.ModMenu;
import dev.btstudio.modmenufix.FixConfig;
import dev.btstudio.modmenufix.MissingParents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Runs once, right after Mod Menu has built its mod maps, and drops the phantom parent
 * entries it created for mods that are not installed.
 */
@Mixin(ModMenu.class)
public abstract class ModMenuMixin {
	@Inject(method = "onInitializeClient", at = @At("TAIL"))
	private void modmenufix$stripMissingParents(CallbackInfo ci) {
		FixConfig config = FixConfig.get();

		if (!config.hideMissingParentMods) {
			return;
		}

		int removed = MissingParents.stripMissingParents(
				ModMenu.MODS,
				ModMenu.PARENT_MAP,
				ModMenu.ROOT_MODS,
				config.countOrphanedChildrenAsRootMods,
				config.logMissingParents
		);

		if (removed > 0) {
			// The title screen counter is cached; it would otherwise keep counting the phantoms.
			ModMenu.clearModCountCache();
		}
	}
}
