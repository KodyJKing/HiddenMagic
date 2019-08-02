package kjk.hiddenmagic;

import kjk.hiddenmagic.blockextension.WorldExtension;
import kjk.hiddenmagic.common.CMath;
import kjk.hiddenmagic.common.CWorld;
import kjk.hiddenmagic.common.Common;
import kjk.hiddenmagic.flow.Flow2;
import kjk.hiddenmagic.magictype.MagicType;
import kjk.hiddenmagic.magictype.MagicTypes;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;

public class ModEvents {
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        randomUpdate(world);
        Flow2.flow(world, MagicTypes.LIFE);
    }

    int updateLCG = 1;
    void randomUpdate(World world) {
        Iterator<Chunk> chunks = Common.getTickableChunks(world);
        if (chunks == null)
            return;

        int randomTickSpeed = world.getGameRules().getInt("randomTickSpeed");
        chunks.forEachRemaining((Chunk chunk) -> {
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

                        IBlockState bs = storage.get(dx, dy, dz);
                        Block block = bs.getBlock();

                        if (block instanceof BlockLeaves)
                            tickLeaves(world, pos);

                    }
                }
            }

        });
    }

    void tickLeaves(World world, BlockPos pos) {
        int light = 0;
        for(int x = -1; x <= 1; x++){
            for(int y = -1; y <= 1; y++){
                for(int z = -1; z <= 1; z++) {
                    BlockPos p = pos.add(x, y, z);
                    if (world.getBlockState(p).getMaterial() == Material.LEAVES)
                        light += CWorld.skyLight(world, p);
                }
            }
        }
//        MagicTypes.LIFE.flowInto(world, pos, light / 27);
        MagicTypes.LIFE.add(world, pos, light * 4 / 27 );
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();

        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = event.getItemStack();
        if (stack.getItem() != Items.STICK)
            return;

        BlockPos pos = event.getPos();
        int magic = MagicTypes.LIFE.get(world, pos);

//        we.clear();

//        double activation = Math.sqrt(magic) / 10;
        double activation = MagicTypes.LIFE.get(world, pos) / Math.sqrt(MagicTypes.LIFE.capacity(world, pos)) / 4;
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
//        Common.message(player, Integer.toString(magic));
//        we.set(pos, ++magic);
    }
}
