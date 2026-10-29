package xmasi.fluidsweep;

import com.google.gson.*;
import fi.dy.masa.malilib.config.*;
import fi.dy.masa.malilib.config.options.*;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.*;
import java.io.*;
import java.util.*;

public final class SweepConfig implements IConfigHandler {
    public static final SweepConfig INSTANCE = new SweepConfig();
    public static final ConfigBoolean LAVA = bool("lava", "Place on lava", "Replace nearby lava source blocks using the selected slime or resin block.");
    public static final ConfigBoolean WATER = bool("water", "Place on water", "Replace nearby water source blocks using the selected slime or resin block.");
    public static final ConfigBoolean GRAVITY_WATER = bool("gravityWater", "Gravity blocks on water", "Place the selected falling block on nearby water sources. Takes priority over solid blocks for water.");
    public static final ConfigBoolean GRAVITY_LAVA = bool("gravityLava", "Gravity blocks on lava", "Place the selected falling block on nearby lava sources. Takes priority over solid blocks for lava.");
    public static final ConfigBoolean CLEANUP = bool("cleanup", "Break placed blocks", "Break only slime and resin placed by Fluid Sweep during this world session. Resin needs normal mining time.");
    public static final ConfigOptionList SOLID = new ConfigOptionList("solidBlock", BlockChoice.SOLID.getFirst(), "Slime or resin. Keep the selected block in your hotbar.", "Replacement block", "Replacement block");
    public static final ConfigOptionList GRAVITY = new ConfigOptionList("gravityBlock", BlockChoice.GRAVITY.getFirst(), "Sand, gravel or concrete powder. Keep it in your hotbar. Concrete powder hardens in water.", "Gravity block", "Gravity block");
    public static final ConfigHotkey OPEN = new ConfigHotkey("openConfig", "F8", "Open Fluid Sweep settings.", "Open settings", "Open settings");
    public static final ConfigHotkey STOP = new ConfigHotkey("stopSweep", "", "Turn off all placement and cleanup modes.", "Stop all modes", "Stop all modes");
    public static final List<IConfigBase> GENERAL = List.of(LAVA, WATER, GRAVITY_WATER, GRAVITY_LAVA, CLEANUP, SOLID, GRAVITY);
    public static final List<ConfigHotkey> HOTKEYS = List.of(OPEN, STOP);
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("fluidsweep.json");
    private static ConfigBoolean bool(String key, String label, String comment) { return new ConfigBoolean(key, false, comment, label, label); }
    public static BlockChoice solid() { return (BlockChoice) SOLID.getOptionListValue(); }
    public static BlockChoice gravity() { return (BlockChoice) GRAVITY.getOptionListValue(); }
    public static void stop() { for (var b : List.of(LAVA, WATER, GRAVITY_WATER, GRAVITY_LAVA, CLEANUP)) b.setBooleanValue(false); INSTANCE.save(); }

    @Override public void load() {
        if (!Files.exists(FILE)) { migrate(); return; }
        try (var reader = Files.newBufferedReader(FILE)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            ConfigUtils.readConfigBase(root, "general", GENERAL);
            ConfigUtils.readHotkeys(root, "hotkeys", HOTKEYS);
        } catch (IOException | RuntimeException e) { FluidSweepClient.LOGGER.error("Could not load Fluid Sweep configuration", e); }
    }
    @Override public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            JsonObject root = new JsonObject();
            ConfigUtils.writeConfigBase(root, "general", GENERAL);
            ConfigUtils.writeHotkeys(root, "hotkeys", HOTKEYS);
            Path temporary = FILE.resolveSibling("fluidsweep.json.tmp");
            Files.writeString(temporary, new GsonBuilder().setPrettyPrinting().create().toJson(root));
            Files.move(temporary, FILE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) { FluidSweepClient.LOGGER.error("Could not save Fluid Sweep configuration", e); }
    }
    private void migrate() {
        Path legacy = FILE.resolveSibling("lavasponge.txt");
        if (Files.isRegularFile(legacy)) {
            try {
                Map<String, ConfigBoolean> map = Map.of("LavaSpongeIsOn", LAVA, "WaterSpongeIsOn", WATER,
                    "GravityWaterSpongeIsOn", GRAVITY_WATER, "GravityLavaSpongeIsOn", GRAVITY_LAVA, "SlimeBlockBreakingEnabled", CLEANUP);
                for (String line : Files.readAllLines(legacy)) {
                    String[] pair = line.trim().split("\\s+", 2);
                    if (pair.length != 2) continue;
                    if (map.containsKey(pair[0])) map.get(pair[0]).setBooleanValue(Boolean.parseBoolean(pair[1]));
                    if (pair[0].equals("GravityWaterBlock")) GRAVITY.setValueFromString(pair[1]);
                }
            } catch (IOException e) { FluidSweepClient.LOGGER.warn("Could not migrate Lava Sponge settings", e); }
        }
        save();
    }
}

