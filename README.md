# Fluid Sweep

<img src="src/main/resources/assets/fluidsweep/icon.png" width="180" alt="Fluid Sweep">

**Clear nearby water and lava sources using the blocks you choose.**

Fluid Sweep is a **client-side Fabric mod by XMasi**, available for Minecraft **1.21 through 26.3**.
Download the file that matches your exact Minecraft version.

## Features

- Replace water and lava source blocks with **slime or resin**. Resin is available on **1.21.4 and newer**; older versions offer slime.
- Control water, lava, gravity-water, and gravity-lava modes independently.
- Use **sand, gravel, or any of the 16 concrete powder colors** in gravity modes.
- Optionally mine slime and resin placed during the current world session. Resin uses normal mining time.
- Choose supported materials with a visual block picker and search by name or block ID.
- Searchable MaLiLib settings, reset buttons, configurable hotkeys, and optional Mod Menu integration.
- Import existing settings from `config/lavasponge.txt` on first launch.

![Visual block picker](docs/block-picker.png)

## Install and use

1. Install Fabric Loader, **Fabric API**, and the **MaLiLib** release for your Minecraft version.
2. Download the matching Fluid Sweep JAR and place it in `mods`.
3. Remove the old Lava Sponge JAR if you are upgrading.
4. Press **F8** or open the settings through **Mod Menu**.
5. Put the selected blocks in your **hotbar** and enable the desired modes.

All modes default to off on a fresh installation. Imported Lava Sponge settings may already be enabled.
Settings are saved in `config/fluidsweep.json`.

The mod targets still water and lava blocks, excludes waterlogged builds, and respects normal block interaction reach.
It uses normal client interactions; servers validate placement and mining. Concrete powder hardens when touching water.
Gravity mode takes priority when both modes target the same fluid.
Cleanup only affects tracked slime/resin placed by this mod during the current world session; tracking clears on disconnect or world change.
On 26.x, block icons become available after joining a world; material selection still works from the main menu.

## Supported builds

| Minecraft | Java | MaLiLib used for the build |
|---|---|---|
| 1.21 | 21 | 0.21.10 |
| 1.21.1 | 21 | 0.21.10 |
| 1.21.2 | 21 | 0.22.8 |
| 1.21.3 | 21 | 0.22.8 |
| 1.21.4 | 21 | 0.23.5 |
| 1.21.5 | 21 | 0.24.3 |
| 1.21.6 | 21 | 0.25.7 |
| 1.21.7 | 21 | 0.25.7 |
| 1.21.8 | 21 | 0.25.7 |
| 1.21.9 | 21 | 0.26.8 |
| 1.21.10 | 21 | 0.26.8 |
| 1.21.11 | 21 | 0.27.20 |
| 26.1 | 25 | 0.28.12 |
| 26.1.1 | 25 | 0.28.12 |
| 26.1.2 | 25 | 0.28.12 |
| 26.2 | 25 | 0.29.6 |
| 26.3 | 25 | 0.30.2 |

Use the matching Fabric API and Mod Menu releases for your Minecraft version. Mod Menu is optional.
Version 1.3.0 is published as **beta** while broader world and server testing continues.

## Build

Use JDK 21 for 1.21.x, or JDK 25 for 26.x (JDK 25 can build all targets):

```sh
bash ./gradlew -Pmc=1.21.6 build
bash ./gradlew -Pmc=26.3 build
```

Windows: use `gradlew.bat` instead. Artifacts appear in `build/<minecraft>/libs/`.
The build generates compatible sources from the shared Yarn or Mojang source set.
GitHub Actions builds every target. Tags beginning with `v` create a prerelease containing the artifacts after all builds pass.

## Links

- [Modrinth](https://modrinth.com/mod/fluid-sweep)
- [Source](https://github.com/XMasi/FluidSweep)
- [Issues](https://github.com/XMasi/FluidSweep/issues)

## License

**LGPL-2.1-only**. See [LICENSE](LICENSE) and [NOTICE.md](NOTICE.md) for the full license and upstream attribution.

