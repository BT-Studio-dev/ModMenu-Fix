package dev.btstudio.modmenufix;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.terraformersmc.modmenu.util.mod.Mod;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MissingParentsTest {
	/** A dummy parent Mod Menu invents for a mod that is not installed ({@code isReal() == false}). */
	private static Mod missing(String id) {
		return fake(id, false);
	}

	private static Mod installed(String id) {
		return fake(id, true);
	}

	private static Mod fake(String id, boolean real) {
		return (Mod) Proxy.newProxyInstance(Mod.class.getClassLoader(), new Class<?>[]{Mod.class}, (proxy, method, args) -> switch (method.getName()) {
			case "getId" -> id;
			case "isReal" -> real;
			case "equals" -> proxy == args[0];
			case "hashCode" -> System.identityHashCode(proxy);
			case "toString" -> "Mod[" + id + "]";
			default -> throw new UnsupportedOperationException(method.getName());
		});
	}

	@Test
	void removesPhantomParentAndKeepsItsInstalledChildrenVisible() {
		Mod phantom = missing("create-addons");
		Mod addon = installed("create-addons-fly");
		Mod other = installed("jei");

		Map<String, Mod> mods = new HashMap<>(Map.of(phantom.getId(), phantom, addon.getId(), addon, other.getId(), other));
		ListMultimap<Mod, Mod> parentMap = Multimaps.synchronizedListMultimap(LinkedListMultimap.create());
		parentMap.put(phantom, addon);
		Map<String, Mod> rootMods = new HashMap<>(Map.of(other.getId(), other));

		int removed = MissingParents.stripMissingParents(mods, parentMap, rootMods, true, false);

		assertEquals(1, removed, "exactly one phantom parent should be removed");
		assertFalse(mods.containsKey("create-addons"), "phantom parent must no longer be listed or counted");
		assertTrue(mods.containsKey("create-addons-fly"), "installed addon must stay installed");
		assertTrue(parentMap.get(phantom).isEmpty(), "child must be unlinked from the missing parent");
		assertFalse(parentMap.values().contains(addon), "child must not stay a child of a removed parent");
		assertTrue(rootMods.containsKey("create-addons-fly"), "child must be re-rooted so it is counted");
		assertEquals(1, rootMods.size(), "unrelated root mods must not be added twice");
	}

	@Test
	void leavesRealParentChildGroupsAlone() {
		Mod parent = installed("create");
		Mod child = installed("create-fly");
		Mod other = installed("jei");

		Map<String, Mod> mods = new HashMap<>(Map.of(parent.getId(), parent, child.getId(), child, other.getId(), other));
		ListMultimap<Mod, Mod> parentMap = Multimaps.synchronizedListMultimap(LinkedListMultimap.create());
		parentMap.put(parent, child);
		Map<String, Mod> rootMods = new HashMap<>(Map.of(other.getId(), other));

		int removed = MissingParents.stripMissingParents(mods, parentMap, rootMods, true, false);

		assertEquals(0, removed, "nothing to remove when every parent is installed");
		assertEquals(3, mods.size(), "no mod must be dropped");
		assertEquals(1, parentMap.get(parent).size(), "real parent keeps its child");
		assertEquals(1, rootMods.size(), "no mod must be re-rooted");
	}

	@Test
	void canKeepOrphansOutOfTheRootCounters() {
		Mod phantom = missing("create-addons");
		Mod addon = installed("create-addons-fly");

		Map<String, Mod> mods = new HashMap<>(Map.of(phantom.getId(), phantom, addon.getId(), addon));
		ListMultimap<Mod, Mod> parentMap = Multimaps.synchronizedListMultimap(LinkedListMultimap.create());
		parentMap.put(phantom, addon);
		Map<String, Mod> rootMods = new HashMap<>();

		MissingParents.stripMissingParents(mods, parentMap, rootMods, false, false);

		assertTrue(rootMods.isEmpty(), "with the option off the child is listed but not counted");
		assertFalse(mods.containsKey("create-addons"), "phantom parent is still removed");
	}
}
