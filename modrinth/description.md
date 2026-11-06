**Mod Menu Fix** is an unofficial, client-side patch for **Mod Menu on Minecraft 26.3**. It fixes what Mod Menu does when a mod names a parent that is not installed — phantom entries, wrong mod counts, a crashing update checker — and it puts the full-width Mods button back on the title screen and in the pause menu.

---

## Why this exists

Plenty of 26.3 modpacks contain add-ons whose main mod is missing — a child mod kept, its parent removed, or a parent id that only exists on another loader. Mod Menu 21 reacts by inventing a stand-in entry for that parent and treating it like a real, installed mod. The result is a mod list that lies to you:

- a phantom row with no version, no description and a placeholder icon, standing for a mod you do not have;
- a **"N mods loaded"** counter on the title screen that is higher than the number of mods the Mods screen lists, because the phantom is counted;
- add-ons of that missing parent are **never counted at all**, since Mod Menu only counts them under their parent;
- adding the missing mod id to `hidden_mods` hides the add-ons you *do* have, because children are only listed underneath their parent;
- a `NullPointerException` in the update checker, thrown from `FabricMod#setUpdateInfo` when one of those add-ons has an update.

Mod Menu Fix cleans that up after Mod Menu has finished loading, in memory, and leaves everything else exactly as it is.

## What it does

**Missing mods stop being mods.** Every parent entry that does not correspond to an installed mod is removed from the mod list and from the mod counter. Nothing you actually have is touched.

**Installed add-ons stay visible.** The children of a removed parent become ordinary top-level entries and are counted there — so a broken parent can no longer make a working mod disappear from your list.

**The update checker stops crashing.** The unguarded `setChildHasUpdate()` call on a missing parent is skipped, with a single warning line naming the mod id instead of a stack trace in the log.

**The Mods button comes back.** Minecraft 26.3 moved its menu icon rows, so Mod Menu now defaults to a 20×20 icon. This restores the full-width **Mods** button on the title screen and in the pause menu — the layout people know from 26.2.

## Menus

Both entries are configurable in `config/modmenufix.json`:

- **`title_screen_button`** — `classic` (default, full-width Mods button under Minecraft Realms), `icon` (the 26.3 icon row), `shrink`, `replace_realms`, or `keep`.
- **`game_menu_button`** — `insert` (default, full-width Mods button in the pause menu), `icon`, or `keep`.

`keep` leaves Mod Menu's own setting completely untouched. This mod selects a style inside Mod Menu's configuration; it does not reimplement any layout.

## What it does not do

- It does **not** reupload, fork, bundle or patch files of Mod Menu. Mod Menu is a required dependency and stays exactly as you installed it; this project is a separate jar that injects a few methods at runtime.
- It does not add features to Mod Menu beyond the fixes listed above.
- It is **not affiliated with Mod Menu or TerraformersMC**. Mod Menu is MIT licensed; this patch is MIT licensed too.

## Requirements

- Minecraft **26.3**, Fabric Loader **0.20.1+**, Java **25**
- **Mod Menu 21.0.0** — required; the mod refuses to load on other versions rather than risk a broken mixin
- Fabric API, because Mod Menu needs it
- **Client side only** — it can stay in your client mod folder for servers, since it never touches the game logic servers see

## Install

1. Install Fabric Loader for 26.3, then Mod Menu 21.0.0 and Fabric API.
2. Drop `modmenufix-1.2.0.jar` into `.minecraft/mods`.
3. Launch the game — `Mod Menu Fix 1.2.0 for Minecraft 26.3 ready` in the log means it is working.

A config file, `config/modmenufix.json`, is created on first launch with every option and its default.

## Size and scope

Nothing is bundled and there are no extra dependencies, so the jar is around 27 KB. One log line per missing parent names the mod ids involved, so a broken modpack is easy to diagnose instead of silently wrong.

---

**Source and issue tracker:** [github.com/BT-Studio-dev/ModMenu-Fix](https://github.com/BT-Studio-dev/ModMenu-Fix)
**License:** MIT
**Built for:** Minecraft 26.3 · Fabric · client only
