package xmasi.fluidsweep;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import java.util.List;

public record BlockChoice(Block block, boolean gravity) implements IConfigOptionListEntry {
    public static final List<BlockChoice> SOLID = java.util.stream.Stream.of(Blocks.SLIME_BLOCK, Registries.BLOCK.get(net.minecraft.util.Identifier.of("minecraft", "resin_block")))
        .filter(b -> b != Blocks.AIR).distinct().map(b -> new BlockChoice(b, false)).toList();
    public static final List<BlockChoice> GRAVITY = List.of(
        Blocks.SAND, Blocks.GRAVEL, Blocks.WHITE_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER,
        Blocks.GRAY_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER,
        Blocks.RED_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER,
        Blocks.LIME_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER,
        Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER,
        Blocks.MAGENTA_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER
    ).stream().map(b -> new BlockChoice(b, true)).toList();
    public String getStringValue() { return Registries.BLOCK.getId(block).toString(); }
    public String getDisplayName() { return block.getName().getString(); }
    public IConfigOptionListEntry cycle(boolean forward) {
        var choices = gravity ? GRAVITY : SOLID;
        return choices.get(Math.floorMod(choices.indexOf(this) + (forward ? 1 : -1), choices.size()));
    }
    public IConfigOptionListEntry fromString(String value) {
        String id = value.contains(":") ? value : "minecraft:" + value;
        return (gravity ? GRAVITY : SOLID).stream().filter(b -> b.getStringValue().equals(id)).findFirst().orElse(gravity ? GRAVITY.getFirst() : SOLID.getFirst());
    }
}

