package dev.btstudio.modmenufix.mixin;

import com.terraformersmc.modmenu.util.mod.Mod;
import com.terraformersmc.modmenu.util.mod.fabric.FabricMod;
import dev.btstudio.modmenufix.FixConfig;
import dev.btstudio.modmenufix.MissingParents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * {@code FabricMod#setUpdateInfo} does {@code ModMenu.MODS.get(parent).setChildHasUpdate()}
 * without a null check, so a mod whose declared parent is not installed throws a
 * {@link NullPointerException} on the update checker thread. The redirect below keeps the
 * notification working for every installed parent and simply skips it for a missing one.
 */
@Mixin(FabricMod.class)
public abstract class FabricModMixin {
	@Redirect(
			method = "setUpdateInfo",
			at = @At(
					value = "INVOKE",
					target = "Lcom/terraformersmc/modmenu/util/mod/Mod;setChildHasUpdate()V"
			)
	)
	private void modmenufix$guardMissingParent(Mod parent) {
		if (parent != null) {
			parent.setChildHasUpdate();
			return;
		}

		if (FixConfig.get().guardUpdateCheckerAgainstMissingParents) {
			Mod self = (Mod) (Object) this;
			MissingParents.noteMissingParentAtRuntime(self.getParent());
			return;
		}

		// Guard disabled: restore stock Mod Menu behaviour.
		parent.setChildHasUpdate();
	}
}
