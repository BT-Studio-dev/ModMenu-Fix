package dev.btstudio.modmenufix;

import com.google.common.collect.ListMultimap;
import com.terraformersmc.modmenu.util.mod.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The fix itself.
 *
 * <p>Mod Menu lets a mod declare a parent in {@code fabric.mod.json}:
 * {@code "custom": {"modmenu": {"parent": "some-mod"}}}. When {@code some-mod} is <em>not
 * installed</em>, Mod Menu 21 (Minecraft 26.3) invents a {@code FabricDummyParentMod} for it
 * and inserts that phantom into {@code ModMenu.MODS}. The phantom is not a real, installed
 * mod, but it is still treated as one:</p>
 *
 * <ul>
 *     <li>it shows up in the mod list as a parent row with no version, no description and a
 *     placeholder icon, even though the mod it stands for is missing;</li>
 *     <li>it is counted by {@code ModMenu.getDisplayedModCount()}, so the "N mods loaded"
 *     counter on the title screen is higher than what the Mods screen lists;</li>
 *     <li>it is never added to {@code ROOT_MODS}, so its installed children are invisible to
 *     the "Showing N mods" counter;</li>
 *     <li>adding it to {@code hidden_mods} hides the installed child mods too, because
 *     children are only ever listed underneath their parent.</li>
 * </ul>
 *
 * <p>This class removes just the phantoms and unlinks their children, which turns the
 * children into ordinary top-level entries: nothing installed disappears, and nothing that
 * is not installed is listed or counted.</p>
 */
public final class MissingParents {
	private static final Logger LOGGER = LoggerFactory.getLogger("Mod Menu Fix/Missing Parents");
	private static final Set<String> REPORTED_PARENTS = new HashSet<>();

	private MissingParents() {
	}

	/**
	 * Removes every non-real (phantom) mod from {@code mods}, unlinks it from
	 * {@code parentMap} and re-roots its installed children in {@code rootMods}.
	 *
	 * @return how many phantom parents were removed
	 */
	public static int stripMissingParents(
			Map<String, Mod> mods,
			ListMultimap<Mod, Mod> parentMap,
			Map<String, Mod> rootMods,
			boolean countOrphansAsRootMods,
			boolean logMissingParents
	) {
		// Collect first: everything below mutates these maps.
		List<Mod> missingParents = new ArrayList<>();
		for (Mod mod : mods.values()) {
			if (!mod.isReal()) {
				missingParents.add(mod);
			}
		}

		for (Mod missing : missingParents) {
			List<Mod> children = new ArrayList<>(parentMap.get(missing));

			// Unlink first: while the children are still in parentMap.values() the Mods screen
			// treats them as children of a parent that is no longer listed and drops them.
			parentMap.removeAll(missing);
			mods.remove(missing.getId());
			rootMods.remove(missing.getId());

			List<String> keptVisible = new ArrayList<>();
			for (Mod child : children) {
				if (!child.isReal()) {
					// Phantom of a phantom; handled in its own iteration.
					continue;
				}

				keptVisible.add(child.getId());

				if (countOrphansAsRootMods) {
					rootMods.put(child.getId(), child);
				}
			}

			if (logMissingParents && REPORTED_PARENTS.add(missing.getId())) {
				LOGGER.warn(
						"'{}' is declared as a parent but is not installed; removed the phantom parent entry and kept {} dependent mod(s) visible at the top level: {}",
						missing.getId(),
						keptVisible.size(),
						keptVisible
				);
			}
		}

		return missingParents.size();
	}

	/**
	 * Called from the update checker guard when a mod's parent entry does not exist in
	 * {@code ModMenu.MODS}. Logged once per parent id so a broken modpack cannot flood the log.
	 */
	public static void noteMissingParentAtRuntime(String parentId) {
		if (!FixConfig.get().logMissingParents || !REPORTED_PARENTS.add(parentId)) {
			return;
		}

		LOGGER.warn("Skipped an update notification for a missing parent mod ('{}' is not installed)", parentId);
	}
}
