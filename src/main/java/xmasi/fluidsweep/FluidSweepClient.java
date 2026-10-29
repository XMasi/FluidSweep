package xmasi.fluidsweep;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.hotkeys.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FluidSweepClient implements ClientModInitializer, IKeybindProvider {
    public static final Logger LOGGER = LoggerFactory.getLogger("fluidsweep");
    @Override public void onInitializeClient() {
        ConfigManager.getInstance().registerConfigHandler("fluidsweep", SweepConfig.INSTANCE);
        SweepConfig.INSTANCE.load();
        InputEventHandler.getKeybindManager().registerKeybindProvider(this);
        SweepConfig.OPEN.getKeybind().setCallback((action, key) -> {
            var mc = MinecraftClient.getInstance();
            mc.setScreen(new SweepScreen(mc.currentScreen)); return true;
        });
        SweepConfig.STOP.getKeybind().setCallback((action, key) -> { SweepConfig.stop(); return true; });
        ClientTickEvents.END_CLIENT_TICK.register(SweepEngine::tick);
        LOGGER.info("Fluid Sweep 1.2.1 for Minecraft 1.21.6 initialized");
    }
    @Override public void addKeysToMap(IKeybindManager manager) { SweepConfig.HOTKEYS.forEach(k -> manager.addKeybindToMap(k.getKeybind())); }
    @Override public void addHotkeys(IKeybindManager manager) { manager.addHotkeysForCategory("fluidsweep", "Fluid Sweep", SweepConfig.HOTKEYS); }
}

