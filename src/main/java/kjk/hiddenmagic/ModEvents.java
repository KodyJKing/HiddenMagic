package kjk.hiddenmagic;

import kjk.hiddenmagic.blockbehaviour.BlockBehaviour;
import kjk.hiddenmagic.blockbehaviour.BlockBehaviours;
import kjk.hiddenmagic.common.CMath;
import kjk.hiddenmagic.common.CWorld;
import kjk.hiddenmagic.common.Common;
import kjk.hiddenmagic.flow.InstantFlow;
import kjk.hiddenmagic.magictype.MagicTypes;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ModEvents {

    static long lastTick = 0;
    static double tickLength = 0;
    static int msgTimer0 = 0;
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        randomUpdate(world);
        MagicTypes.LIFE.update(world);

//        long time = System.currentTimeMillis();
//        long dt = time - lastTick;
//        tickLength = dt * 0.1 + tickLength * 0.9;
//        lastTick = time;
//        if (msgTimer0++ % 20 == 0)
//            System.out.println(
//                    String.format("Tick length: %.2f", tickLength)
//            );
    }

    int updateLCG = 1;
    void randomUpdate(World world) {
        Iterator<Chunk> chunks = Common.getTickableChunks(world);
        if (chunks == null)
            return;

        int randomTickSpeed = world.getGameRules().getInt("randomTickSpeed");
        chunks.forEachRemaining((chunk) -> {
            int x = chunk.x * 16;
            int z = chunk.z * 16;

            for (ExtendedBlockStorage storage : chunk.getBlockStorageArray())
            {
                if (storage != Chunk.NULL_BLOCK_STORAGE)
                {
                    for (int i = 0; i < randomTickSpeed; i++)
                    {
                        this.updateLCG = this.updateLCG * 3 + 1013904223;
                        int magic = this.updateLCG >> 2;
                        int dx = magic & 15;
                        int dz = magic >> 8 & 15;
                        int dy = magic >> 16 & 15;
                        BlockPos pos = new BlockPos(dx + x, dy + storage.getYLocation(), dz + z);
                        BlockBehaviours.randomTick(world, pos);
                    }
                }
            }

        });
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();

        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = event.getItemStack();
        if (stack.getItem() != Items.STICK)
            return;

        boolean shiftClickClears = false;
        if (shiftClickClears && player.isSneaking()) {
            if (world.isRemote) {
                System.out.println("Clearing life magic.");
                MagicTypes.LIFE.clear();
            }
            return;
        }

        BlockPos pos = event.getPos();
        int magic = MagicTypes.LIFE.get(world, pos);

        double activation = MagicTypes.LIFE.get(world, pos) / Math.sqrt(MagicTypes.LIFE.capacity(world, pos)) / 4;
        activation = Math.max(0, activation);
        activation = Math.min(100, activation);
        for (int i = 0; i < activation; i++) {
            double vx = CMath.rand.nextGaussian() * 0.06D;
            double vy = CMath.rand.nextGaussian() * 0.06D;
            double vz = CMath.rand.nextGaussian() * 0.06D;
            BlockPos pos2 = pos.offset(event.getFace());
            world.spawnParticle(
                    EnumParticleTypes.VILLAGER_HAPPY,
                    (double)((float)pos2.getX() + CMath.rand.nextFloat()),
                    (double)pos2.getY() + (double)CMath.rand.nextFloat(),
                    (double)((float)pos2.getZ() + CMath.rand.nextFloat()),
                    vx, vy, vz);
        }

        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);

        if (world.isRemote)
            return;

        System.out.println("Magic level = " + Integer.toString(magic));
    }

    @SubscribeEvent
    public void onPlaceBlock(BlockEvent.NeighborNotifyEvent event) {
        MagicTypes.LIFE.notifyChange(event.getWorld(), event.getPos());
    }
}
