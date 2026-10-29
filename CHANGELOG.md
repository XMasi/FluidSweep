# 1.3.0 — Multi-version beta

- Add separate Fabric builds for all 17 Minecraft releases from 1.21 through 26.3.
- Offer slime on 1.21–1.21.3, and slime/resin on 1.21.4 and newer.
- Adapt inventory selection and rendering to the corresponding Minecraft APIs.
- Add Mojang-named sources and Java 25 support for 26.x.
- Fix the 26.x main-menu block picker when item components are not yet available.
- Add a GitHub Actions build matrix and tagged prereleases.

# 1.2.1+1.21.6

- Fix the missing Mod Menu icon by centering the original artwork on a square transparent canvas.
- Remove the Contributors section from the Minecraft mod details; preserve attribution in NOTICE.md.

# 1.2.0+1.21.6 — Fluid Sweep

- Rename the mod to Fluid Sweep, with XMasi as author and the supplied logo.
- Add resin as an alternative to slime for water and lava source replacement.
- Retain water, lava, gravity-water, gravity-lava, and optional cleanup modes.
- Retain sand, gravel, and all 16 concrete powder colors for gravity placement.
- Replace the old button menu with a MaLiLib configuration screen and a searchable icon picker.
- Add an F8 settings hotkey, customizable controls, and optional stop shortcut.
- Import legacy Lava Sponge settings into `fluidsweep.json`.
- Support progressive mining of placed resin; restrict cleanup to tracked slime and resin.
- Clear placement tracking when changing worlds, and stop processing while menus are open.
- Exclude waterlogged blocks and respect normal interaction reach.
- Give gravity mode priority when two modes target the same fluid.
- Correct license metadata to match the supplied LGPL 2.1 license; preserve upstream credits.
- Point Source and Issues links to XMasi/FluidSweep.

