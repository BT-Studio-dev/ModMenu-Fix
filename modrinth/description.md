# Mod Menu Fix

A small client-side patch for [Mod Menu](https://modrinth.com/mod/modmenu) on **Minecraft 26.3**. It fixes what happens when a mod declares a parent that is not installed, and puts the Mods button back where you expect it.

Nothing is bundled or shaded: Mod Menu stays a normal dependency and is patched in memory. No extra libraries, no config screen of its own.

## What was wrong

A mod can name a parent in its `fabric.mod.json`:

```json
"custom": { "modmenu": { "parent": "some-other-mod" } }
```

That is how Mod Menu groups add-ons under their main mod. When the parent is **not installed**, Mod Menu 21 invents a stand-in entry for it and treats it like an installed mod:

- a phantom row with no version, no description and a placeholder icon appears in the mod list;
- the phantom is counted, so the "N mods loaded" counter on the title screen is higher than what the Mods screen lists;
- the add-ons that *are* installed are not counted at all;
- putting the missing mod id in `hidden_mods` hides the installed add-ons too, because children are only listed under their parent;
- the update checker throws a `NullPointerException` when a mod with a missing parent has an update.

## What this mod changes

- **Missing mods stop being listed.** Phantom parent entries are removed from the mod list and from the mod counter.
- **Installed add-ons stay visible.** The children of a missing parent become normal top-level mods and are counted there — nothing you actually have disappears.
- **No update-checker crash.** The unguarded `setChildHasUpdate()` call on a missing parent is skipped, with one warning line in the log naming the mod id.
- **The Mods button comes back.** Minecraft 26.3 moved its menu icon rows, so Mod Menu now defaults to a 20×20 icon. This restores the full-width **Mods** button on the title screen and in the pause menu.

## Menus

| `config/modmenufix.json` | Default | Effect |
| --- | --- | --- |
| `title_screen_button` | `classic` | full-width Mods button under Minecraft Realms, the 26.2 look |
| `game_menu_button` | `insert` | full-width Mods button in the pause menu |

Also accepted: `icon` (the 26.3 icon row), `shrink`, `replace_realms`, and `keep` — which leaves Mod Menu's own setting untouched.

## Requirements

- Minecraft **26.3**, Fabric Loader **0.20.1+**, Java **25**
- **Mod Menu 21.0.0** (required, not bundled) and Fabric API, which Mod Menu itself needs
- Client only — safe to leave out of a server's mod folder

## Install

1. Install Fabric Loader for 26.3, then Mod Menu 21.0.0 and Fabric API.
2. Drop `modmenufix-1.2.0.jar` into `.minecraft/mods`.
3. The log line `Mod Menu Fix 1.2.0 for Minecraft 26.3 ready` confirms it is active.

Config is created on first launch at `config/modmenufix.json`.

## Links

- Source and issues: [github.com/BT-Studio-dev/ModMenu-Fix](https://github.com/BT-Studio-dev/ModMenu-Fix)
- Downloads: [Releases](https://github.com/BT-Studio-dev/ModMenu-Fix/releases)
- Mod Menu: [github.com/TerraformersMC/ModMenu](https://github.com/TerraformersMC/ModMenu) (MIT)

## License

MIT. Mod Menu is MIT and © its authors; this project only injects into it.
