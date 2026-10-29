package xmasi.fluidsweep;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import java.util.*;

/** Client-side block interactions; no fluid or inventory changes are made directly. */
public final class SweepEngine {
    private static final int SCAN_RADIUS = 6;
    private static final int MAX_INTERACTIONS = 12;
    private static final Map<BlockPos, Placement> PLACED = new LinkedHashMap<>();
    private static ClientLevel sessionWorld;
    private static BlockPos mining;
    private static long ticks;
    private static final class Placement {
        final Block block;
        final long tick;
        boolean confirmed;
        Placement(Block block, long tick) { this.block = block; this.tick = tick; }
    }
    public static void tick(Minecraft mc) {
        if (sessionWorld != mc.level) {
            PLACED.clear(); mining = null; ticks = 0; sessionWorld = mc.level;
        }
        if (mc.player == null || mc.level == null || mc.gameMode == null) return;
        ticks++;
        if (mc.gui.screen() != null || mc.isPaused() || mc.player.isSpectator()) { cancelMining(mc); return; }
        reconcile(mc);
        boolean lava = SweepConfig.LAVA.getBooleanValue() || SweepConfig.GRAVITY_LAVA.getBooleanValue();
        boolean water = SweepConfig.WATER.getBooleanValue() || SweepConfig.GRAVITY_WATER.getBooleanValue();
        if (lava || water) {
            int interactions = 0;
            BlockPos origin = mc.player.blockPosition();
            for (BlockPos mutable : BlockPos.betweenClosed(origin.offset(-SCAN_RADIUS, -SCAN_RADIUS, -SCAN_RADIUS), origin.offset(SCAN_RADIUS, SCAN_RADIUS, SCAN_RADIUS))) {
                if (interactions >= MAX_INTERACTIONS || PLACED.size() >= 8192) break;
                BlockPos pos = mutable.immutable();
                if (origin.distSqr(pos) >= SCAN_RADIUS * SCAN_RADIUS || !inReach(mc, pos) || PLACED.containsKey(pos)) continue;
                var state = mc.level.getBlockState(pos);
                // Only full fluid blocks: do not replace waterlogged builds or flowing fluid.
                boolean targetLava = lava && state.is(Blocks.LAVA) && state.getFluidState().isSource();
                boolean targetWater = water && state.is(Blocks.WATER) && state.getFluidState().isSource();
                if (!targetLava && !targetWater) continue;
                boolean gravity = targetLava ? SweepConfig.GRAVITY_LAVA.getBooleanValue() : SweepConfig.GRAVITY_WATER.getBooleanValue();
                Block block = gravity ? SweepConfig.gravity().block() : SweepConfig.solid().block();
                if (!selectHotbar(mc, block)) continue;
                var hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.DOWN, pos, false);
                var result = mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hit);
                interactions++;
                if (result.consumesAction()) {
                    mc.player.swing(InteractionHand.MAIN_HAND, net.minecraft.world.item.component.SwingAnimation.DEFAULT, true);
                    // Gravity blocks are tracked briefly to avoid duplicate placement attempts.
                    PLACED.put(pos, new Placement(block, ticks));
                }
            }
        }
        if (SweepConfig.CLEANUP.getBooleanValue()) cleanup(mc); else cancelMining(mc);
    }
    private static boolean inReach(Minecraft mc, BlockPos pos) {
        double reach = mc.player.blockInteractionRange();
        return mc.player.getEyePosition().distanceToSqr(Vec3.atCenterOf(pos)) <= reach * reach;
    }
    private static boolean selectHotbar(Minecraft mc, Block block) {
        var inventory = mc.player.getInventory();
        if (inventory.getSelectedItem().is(block.asItem())) return true;
        for (int slot = 0; slot < 9; slot++) {
            if (inventory.getItem(slot).is(block.asItem())) {
                inventory.setSelectedSlot(slot);
                mc.getConnection().send(new ServerboundSetCarriedItemPacket(slot));
                return true;
            }
        }
        return false;
    }
    private static boolean solid(Block block) { return BlockChoice.SOLID.stream().anyMatch(choice -> choice.block() == block); }
    private static void reconcile(Minecraft mc) {
        var iterator = PLACED.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next(); var placed = entry.getValue();
            if (!mc.level.hasChunkAt(entry.getKey())) continue;
            if (mc.level.getBlockState(entry.getKey()).is(placed.block)) placed.confirmed = true;
            else if (placed.confirmed || ticks - placed.tick > 40) { iterator.remove(); continue; }
            if (!solid(placed.block) && ticks - placed.tick > 40) iterator.remove();
        }
    }
    private static void cleanup(Minecraft mc) {
        if (mining != null) {
            var placement = PLACED.get(mining);
            if (placement == null || !mc.level.getBlockState(mining).is(placement.block) || !inReach(mc, mining)) cancelMining(mc);
        }
        if (mining == null) {
            for (var entry : PLACED.entrySet()) {
                var p = entry.getValue();
                if (solid(p.block) && p.confirmed && ticks - p.tick >= 2 && inReach(mc, entry.getKey())) {
                    mining = entry.getKey(); mc.gameMode.startDestroyBlock(mining, Direction.DOWN); break;
                }
            }
        } else mc.gameMode.continueDestroyBlock(mining, Direction.DOWN);
        if (mining != null) mc.player.swing(InteractionHand.MAIN_HAND, net.minecraft.world.item.component.SwingAnimation.DEFAULT, true);
    }
    private static void cancelMining(Minecraft mc) {
        if (mining != null) { mc.gameMode.stopDestroyBlock(); mining = null; }
    }
}

