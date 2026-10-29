package xmasi.fluidsweep;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.registries.BuiltInRegistries;
import java.util.List;

public record BlockChoice(Block block, boolean gravity) implements IConfigOptionListEntry {
    public static final List<BlockChoice> SOLID = java.util.stream.Stream.of(Blocks.SLIME_BLOCK, BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "resin_block")))
        .filter(b -> b != Blocks.AIR).distinct().map(b -> new BlockChoice(b, false)).toList();
    public static final List<BlockChoice> GRAVITY = List.of(
        Blocks.SAND, Blocks.GRAVEL, BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "white_concrete_powder")), BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "light_gray_concrete_powder")),
        BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "gray_concrete_powder")), BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "black_concrete_powder")), BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "brown_concrete_powder")),
        BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "red_concrete_powder")), BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "orange_concrete_powder")), BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "yellow_concrete_powder")),
        BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "lime_concrete_powder")), BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "green_concrete_powder")), BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "cyan_concrete_powder")),
        BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "light_blue_concrete_powder")), BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "blue_concrete_powder")), BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "purple_concrete_powder")),
        BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "magenta_concrete_powder")), BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "pink_concrete_powder"))
    ).stream().map(b -> new BlockChoice(b, true)).toList();
    public String getStringValue() { return BuiltInRegistries.BLOCK.getKey(block).toString(); }
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

