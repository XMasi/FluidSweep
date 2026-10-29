package xmasi.fluidsweep;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import java.util.*;

/** Searchable icon picker inspired by LPCTools; restricted to supported materials. */
public final class BlockPickerScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget search;
    private boolean gravity;
    private String query = "";
    private final Map<ButtonWidget, BlockChoice> icons = new LinkedHashMap<>();
    public BlockPickerScreen(Screen parent) { super(Text.literal("Fluid Sweep — Choose blocks")); this.parent = parent; }
    @Override protected void init() {
        int left = width / 2 - 150;
        addDrawableChild(ButtonWidget.builder(Text.literal("Slime / Resin"), b -> { gravity = false; rebuild(); }).dimensions(left, 36, 146, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Gravity blocks"), b -> { gravity = true; rebuild(); }).dimensions(left + 154, 36, 146, 20).build());
        search = new TextFieldWidget(textRenderer, left, 65, 300, 20, Text.literal("Search blocks"));
        search.setPlaceholder(Text.literal("Search by name or block ID…"));
        search.setMaxLength(100); search.setText(query);
        addDrawableChild(search);
        search.setChangedListener(value -> { query = value; refreshIcons(); });
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close()).dimensions(width / 2 - 60, height - 30, 120, 20).build());
        refreshIcons();
    }
    private void rebuild() { clearChildren(); icons.clear(); init(); }
    private void refreshIcons() {
        icons.keySet().forEach(this::remove); icons.clear();
        var list = gravity ? BlockChoice.GRAVITY : BlockChoice.SOLID;
        String term = query.toLowerCase(Locale.ROOT).trim();
        int index = 0;
        for (BlockChoice choice : list) {
            if (!choice.getDisplayName().toLowerCase(Locale.ROOT).contains(term) && !choice.getStringValue().contains(term)) continue;
            int x = width / 2 - 150 + index % 9 * 34;
            int y = 98 + index / 9 * 34;
            var button = ButtonWidget.builder(Text.empty(), b -> {
                (gravity ? SweepConfig.GRAVITY : SweepConfig.SOLID).setOptionListValue(choice);
                SweepConfig.INSTANCE.save();
            }).dimensions(x, y, 28, 28).tooltip(Tooltip.of(Text.literal(choice.getDisplayName() + "\n" + choice.getStringValue()))).build();
            addDrawableChild(button); icons.put(button, choice); index++;
        }
    }
    @Override public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xEF17191F);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 15, 0xFFFFFFFF);
        for (var entry : icons.entrySet()) {
            var button = entry.getKey(); var choice = entry.getValue();
            if (choice.equals(gravity ? SweepConfig.gravity() : SweepConfig.solid()))
                outline(context, button.getX(), button.getY());
            context.drawItem(choice.block().asItem().getDefaultStack(), button.getX() + 6, button.getY() + 6);
        }
        String selected = (gravity ? SweepConfig.gravity() : SweepConfig.solid()).getDisplayName();
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Selected: " + selected), width / 2, height - 62, 0xFF85DD9A);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Keep the selected block in your hotbar"), width / 2, height - 48, 0xFFB9BFC9);
        if (icons.isEmpty()) context.drawCenteredTextWithShadow(textRenderer, Text.literal("No matching blocks"), width / 2, 110, 0xFFB9BFC9);
    }
    @Override public void close() { SweepConfig.INSTANCE.save(); client.setScreen(parent); }
    @Override public boolean shouldPause() { return false; }
    private static void outline(DrawContext c, int x, int y) {
        c.fill(x,y,x+28,y+1,0xFF85DD9A); c.fill(x,y+27,x+28,y+28,0xFF85DD9A);
        c.fill(x,y,x+1,y+28,0xFF85DD9A); c.fill(x+27,y,x+28,y+28,0xFF85DD9A);
    }
}

