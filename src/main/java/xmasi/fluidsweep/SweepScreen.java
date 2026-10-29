package xmasi.fluidsweep;

import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import net.minecraft.client.gui.screen.Screen;
import java.util.List;

public final class SweepScreen extends GuiConfigsBase {
    private boolean hotkeys;
    public SweepScreen(Screen parent) { super(10, 52, "fluidsweep", parent, "Fluid Sweep — XMasi"); }
    @Override public void initGui() {
        super.initGui();
        addButton(new ButtonGeneric(10, 27, 90, 20, "Settings"), (b, m) -> changeTab(false));
        addButton(new ButtonGeneric(104, 27, 90, 20, "Hotkeys"), (b, m) -> changeTab(true));
        addButton(new ButtonGeneric(198, 27, 110, 20, "Choose blocks…"), (b, m) -> client.setScreen(new BlockPickerScreen(this)));
    }
    private void changeTab(boolean value) {
        SweepConfig.INSTANCE.save(); hotkeys = value;
        reCreateListWidget(); getListWidget().resetScrollbarPosition(); initGui();
    }
    @Override protected int getConfigWidth() { return 180; }
    @Override protected boolean useKeybindSearch() { return hotkeys; }
    @Override public List<ConfigOptionWrapper> getConfigs() { return ConfigOptionWrapper.createFor(hotkeys ? SweepConfig.HOTKEYS : SweepConfig.GENERAL); }
    @Override protected void onSettingsChanged() { super.onSettingsChanged(); SweepConfig.INSTANCE.save(); }
    @Override public void removed() { super.removed(); SweepConfig.INSTANCE.save(); }
    @Override public boolean shouldPause() { return false; }
}

