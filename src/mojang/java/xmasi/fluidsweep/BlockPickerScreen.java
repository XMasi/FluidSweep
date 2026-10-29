package xmasi.fluidsweep;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import java.util.*;

/** Searchable icon picker inspired by LPCTools; restricted to supported materials. */
public final class BlockPickerScreen extends Screen {
    private final Screen parent;
    private EditBox search;
    private boolean gravity;
    private String query = "";
    private final Map<Button, BlockChoice> icons = new LinkedHashMap<>();
    public BlockPickerScreen(Screen parent) { super(Component.literal("Fluid Sweep — Choose blocks")); this.parent = parent; }
    @Override protected void init() {
        int left = width / 2 - 150;
        addRenderableWidget(Button.builder(Component.literal("Slime / Resin"), b -> { gravity = false; rebuild(); }).bounds(left, 36, 146, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Gravity blocks"), b -> { gravity = true; rebuild(); }).bounds(left + 154, 36, 146, 20).build());
        search = new EditBox(font, left, 65, 300, 20, Component.literal("Search blocks"));
        search.setHint(Component.literal("Search by name or block ID…"));
        search.setMaxLength(100); search.setValue(query);
        addRenderableWidget(search);
        search.setResponder(value -> { query = value; refreshIcons(); });
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose()).bounds(width / 2 - 60, height - 30, 120, 20).build());
        refreshIcons();
    }
    private void rebuild() { clearWidgets(); icons.clear(); init(); }
    private void refreshIcons() {
        icons.keySet().forEach(this::removeWidget); icons.clear();
        var list = gravity ? BlockChoice.GRAVITY : BlockChoice.SOLID;
        String term = query.toLowerCase(Locale.ROOT).trim();
        int index = 0;
        for (BlockChoice choice : list) {
            if (!choice.getDisplayName().toLowerCase(Locale.ROOT).contains(term) && !choice.getStringValue().contains(term)) continue;
            int x = width / 2 - 150 + index % 9 * 34;
            int y = 98 + index / 9 * 34;
            var button = Button.builder(Component.empty(), b -> {
                (gravity ? SweepConfig.GRAVITY : SweepConfig.SOLID).setOptionListValue(choice);
                SweepConfig.INSTANCE.save();
            }).bounds(x, y, 28, 28).tooltip(Tooltip.create(Component.literal(choice.getDisplayName() + "\n" + choice.getStringValue()))).build();
            addRenderableWidget(button); icons.put(button, choice); index++;
        }
    }
    @Override public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xEF17191F);
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(font, title, width / 2, 15, 0xFFFFFFFF);
        for (var entry : icons.entrySet()) {
            var button = entry.getKey(); var choice = entry.getValue();
            if (choice.equals(gravity ? SweepConfig.gravity() : SweepConfig.solid()))
                outline(context, button.getX(), button.getY());
            if (minecraft.level != null) {
                context.item(choice.block().asItem().getDefaultInstance(), button.getX() + 6, button.getY() + 6);
            } else {
                context.centeredText(font, choice.getDisplayName().substring(0, 1), button.getX() + 14, button.getY() + 10, 0xFFFFFFFF);
            }
        }
        String selected = (gravity ? SweepConfig.gravity() : SweepConfig.solid()).getDisplayName();
        context.centeredText(font, Component.literal("Selected: " + selected), width / 2, height - 62, 0xFF85DD9A);
        context.centeredText(font, Component.literal(minecraft.level == null ? "Block icons appear after joining a world" : "Keep the selected block in your hotbar"), width / 2, height - 48, 0xFFB9BFC9);
        if (icons.isEmpty()) context.centeredText(font, Component.literal("No matching blocks"), width / 2, 110, 0xFFB9BFC9);
    }
    @Override public void onClose() { SweepConfig.INSTANCE.save(); minecraft.setScreenAndShow(parent); }
    @Override public boolean isPauseScreen() { return false; }
    private static void outline(GuiGraphicsExtractor c, int x, int y) {
        c.fill(x,y,x+28,y+1,0xFF85DD9A); c.fill(x,y+27,x+28,y+28,0xFF85DD9A);
        c.fill(x,y,x+1,y+28,0xFF85DD9A); c.fill(x+27,y,x+28,y+28,0xFF85DD9A);
    }
}

