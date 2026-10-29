package xmasi.fluidsweep;
import com.terraformersmc.modmenu.api.*;
public final class ModMenuIntegration implements ModMenuApi {
    @Override public ConfigScreenFactory<?> getModConfigScreenFactory() { return SweepScreen::new; }
}

