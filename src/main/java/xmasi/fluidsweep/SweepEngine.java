package xmasi.fluidsweep;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import java.util.*;

/** Client-side block interactions; no fluid or inventory changes are made directly. */
public final class SweepEngine {
    private static final int SCAN_RADIUS = 6;
    private static final int MAX_INTERACTIONS = 12;
    private static final Map<BlockPos, Placement> PLACED = new LinkedHashMap<>();
    private static ClientWorld sessionWorld;
    private static BlockPos mining;
    private static long ticks;
    private static final class Placement {
        final Block block;
        final long tick;
        boolean confirmed;
        Placement(Block block, long tick) { this.block = block; this.tick = tick; }
    }
    public static void tick(MinecraftClient mc) {
        if (sessionWorld != mc.world) {
            PLACED.clear(); mining = null; ticks = 0; sessionWorld = mc.world;
        }
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        ticks++;
        if (mc.currentScreen != null || mc.isPaused() || mc.player.isSpectator()) { cancelMining(mc); return; }
        reconcile(mc);
        boolean lava = SweepConfig.LAVA.getBooleanValue() || SweepConfig.GRAVITY_LAVA.getBooleanValue();
        boolean water = SweepConfig.WATER.getBooleanValue() || SweepConfig.GRAVITY_WATER.getBooleanValue();
        if (lava || water) {
            int interactions = 0;
            BlockPos origin = mc.player.getBlockPos();
            for (BlockPos mutable : BlockPos.iterateOutwards(origin, SCAN_RADIUS, SCAN_RADIUS, SCAN_RADIUS)) {
                if (interactions >= MAX_INTERACTIONS || PLACED.size() >= 8192) break;
                BlockPos pos = mutable.toImmutable();
                if (origin.getSquaredDistance(pos) >= SCAN_RADIUS * SCAN_RADIUS || !inReach(mc, pos) || PLACED.containsKey(pos)) continue;
                var state = mc.world.getBlockState(pos);
                // Only full fluid blocks: do not replace waterlogged builds or flowing fluid.
                boolean targetLava = lava && state.isOf(Blocks.LAVA) && state.getFluidState().isStill();
                boolean targetWater = water && state.isOf(Blocks.WATER) && state.getFluidState().isStill();
                if (!targetLava && !targetWater) continue;
                boolean gravity = targetLava ? SweepConfig.GRAVITY_LAVA.getBooleanValue() : SweepConfig.GRAVITY_WATER.getBooleanValue();
                Block block = gravity ? SweepConfig.gravity().block() : SweepConfig.solid().block();
                if (!selectHotbar(mc, block)) continue;
                var hit = new BlockHitResult(Vec3d.ofCenter(pos), Direction.DOWN, pos, false);
                var result = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
                interactions++;
                if (result.isAccepted()) {
                    mc.player.swingHand(Hand.MAIN_HAND);
                    // Gravity blocks are tracked briefly to avoid duplicate placement attempts.
                    PLACED.put(pos, new Placement(block, ticks));
                }
            }
        }
        if (SweepConfig.CLEANUP.getBooleanValue()) cleanup(mc); else cancelMining(mc);
    }
    private static boolean inReach(MinecraftClient mc, BlockPos pos) {
        double reach = mc.player.getBlockInteractionRange();
        return mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(pos)) <= reach * reach;
    }
    private static boolean selectHotbar(MinecraftClient mc, Block block) {
        var inventory = mc.player.getInventory();
        if (inventory.getSelectedStack().isOf(block.asItem())) return true;
        for (int slot = 0; slot < 9; slot++) {
            if (inventory.getStack(slot).isOf(block.asItem())) {
                inventory.setSelectedSlot(slot);
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                return true;
            }
        }
        return false;
    }
    private static boolean solid(Block block) { return BlockChoice.SOLID.stream().anyMatch(choice -> choice.block() == block); }
    private static void reconcile(MinecraftClient mc) {
        var iterator = PLACED.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next(); var placed = entry.getValue();
            if (!mc.world.isChunkLoaded(entry.getKey())) continue;
            if (mc.world.getBlockState(entry.getKey()).isOf(placed.block)) placed.confirmed = true;
            else if (placed.confirmed || ticks - placed.tick > 40) { iterator.remove(); continue; }
            if (!solid(placed.block) && ticks - placed.tick > 40) iterator.remove();
        }
    }
    private static void cleanup(MinecraftClient mc) {
        if (mining != null) {
            var placement = PLACED.get(mining);
            if (placement == null || !mc.world.getBlockState(mining).isOf(placement.block) || !inReach(mc, mining)) cancelMining(mc);
        }
        if (mining == null) {
            for (var entry : PLACED.entrySet()) {
                var p = entry.getValue();
                if (solid(p.block) && p.confirmed && ticks - p.tick >= 2 && inReach(mc, entry.getKey())) {
                    mining = entry.getKey(); mc.interactionManager.attackBlock(mining, Direction.DOWN); break;
                }
            }
        } else mc.interactionManager.updateBlockBreakingProgress(mining, Direction.DOWN);
        if (mining != null) mc.player.swingHand(Hand.MAIN_HAND);
    }
    private static void cancelMining(MinecraftClient mc) {
        if (mining != null) { mc.interactionManager.cancelBlockBreaking(); mining = null; }
    }
}

