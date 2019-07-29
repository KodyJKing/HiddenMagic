package kjk.hiddenmagic;

import kjk.hiddenmagic.blockextension.BlockExtensions;
import kjk.hiddenmagic.blockextension.WorldExtension;
import kjk.hiddenmagic.chunkextension.ChunkExtension;
import kjk.hiddenmagic.common.CMath;
import kjk.hiddenmagic.common.CWorld;
import kjk.hiddenmagic.common.Common;
import kjk.hiddenmagic.magictype.MagicTypes;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

public class ModEvents {
    int updateLCG = 1;
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;

        Iterator<Chunk> chunks = null;
        try {
            chunks = Common.getTickableChunks(world);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (chunks == null)
            return;

        chunks.forEachRemaining((Chunk chunk) -> {
            int x = chunk.x * 16;
            int z = chunk.z * 16;

            for (ExtendedBlockStorage storage : chunk.getBlockStorageArray()) {
                if (storage != Chunk.NULL_BLOCK_STORAGE) {
                    for (int i = 0; i < 3; i++) {
                        this.updateLCG = this.updateLCG * 3 + 1013904223;
                        int magic = this.updateLCG >> 2;
                        int dx = magic & 15;
                        int dz = magic >> 8 & 15;
                        int dy = magic >> 16 & 15;

                        BlockPos pos = new BlockPos(dx + x, dy + storage.getYLocation(), dz + z);

                        tick(world, pos);
                    }
                }
            }

        });
    }

    void tick(World world, BlockPos pos) {
        IBlockState bs = world.getBlockState(pos);
        Block block = bs.getBlock();

        if (block instanceof BlockLeaves)
            tickLeaves(world, pos);

    }

    void tickLeaves(World world, BlockPos pos) {
        int light = 0;
        for(int x = -1; x <= 1; x++) {
            for(int y = -1; y <= 1; y++) {
                for(int z = -1; z <= 1; z++) {
                    BlockPos p = pos.add(x, y, z);
                    if (world.getBlockState(p).getMaterial() == Material.LEAVES)
                        light += CWorld.skyLight(world, p);
                }
            }
        }
        MagicTypes.LIFE.flowInto(world, pos, light / 27);
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        if (world.isRemote)
            return;
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = event.getItemStack();
        if (stack.getItem() != Items.STICK)
            return;
        BlockPos pos = event.getPos();

        // --------------------
        Chunk chunk = world.getChunkFromBlockCoords(pos);
        ChunkExtension ce = chunk.getCapability(ChunkExtension.capability, EnumFacing.UP).get();
        chunk.markDirty();
        if (ce != null) {
            System.out.println("CE exists!");
            System.out.println(ce.foo);
            ce.foo++;
        }
        // --------------------


        WorldExtension<Integer> we = BlockExtensions.LIFE_MAGIC.getWorldExtension(world);
        int magic = we.get(pos);
        Common.message(player, Integer.toString(magic));
////        we.set(pos, ++magic);

//        double activation = Math.log(magic + 1);
//        for (int i = 0; i < activation; i++) {
//            double vx = CMath.rand.nextGaussian() * 0.02D;
//            double vy = CMath.rand.nextGaussian() * 0.02D;
//            double vz = CMath.rand.nextGaussian() * 0.02D;
//            world.spawnParticle(
//                    EnumParticleTypes.VILLAGER_HAPPY,
//                    (double)((float)pos.getX() + CMath.rand.nextFloat()),
//                    (double)pos.getY() + (double)CMath.rand.nextFloat(),
//                    (double)((float)pos.getZ() + CMath.rand.nextFloat()),
//                    vx, vy, vz);
//        }
    }

    @SubscribeEvent
    public void attatchChunkCapabilities(AttachCapabilitiesEvent<Chunk> event) {
        event.addCapability(new ResourceLocation(HiddenMagic.MODID, "chunkextension"), new ICapabilityProvider() {
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                return true;
            }

            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                return capability.getDefaultInstance();
            }
        });
    }
}
