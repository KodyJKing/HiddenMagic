package kjk.hiddenmagic;

import kjk.hiddenmagic.blockextension.BlockExtensions;
import kjk.hiddenmagic.blockextension.WorldExtension;
import kjk.hiddenmagic.common.Common;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

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

            for (ExtendedBlockStorage storage : chunk.getBlockStorageArray())
            {
                if (storage != Chunk.NULL_BLOCK_STORAGE)
                {
                    for (int i = 0; i < 3; i++)
                    {
                        this.updateLCG = this.updateLCG * 3 + 1013904223;
                        int magic = this.updateLCG >> 2;
                        int dx = magic & 15;
                        int dz = magic >> 8 & 15;
                        int dy = magic >> 16 & 15;

                        BlockPos pos = new BlockPos(dx + x, dy + storage.getYLocation(), dz + z);

                        IBlockState bs = storage.get(dx, dy, dz);
                        Block block = bs.getBlock();

                    }
                }
            }

        });
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

        int dimension = world.provider.getDimension();
        WorldExtension<Integer> we = BlockExtensions.LIFE_MAGIC.getWorldExtension(world);
        int magic = we.get(pos);
        Common.message(player, Integer.toString(magic));
        we.set(pos, ++magic);
    }
}
