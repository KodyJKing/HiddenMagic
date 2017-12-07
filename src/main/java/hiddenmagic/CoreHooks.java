package hiddenmagic;

import hiddenmagic.common.CWorld;
import hiddenmagic.magictype.MagicTypes;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

// Hooks are called by hiddenmagiccore.
public class CoreHooks {
    public static void leafTick(BlockLeaves block, World world, BlockPos pos, IBlockState state, Random rand) {
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
        MagicTypes.LIFE.flowInto(world, pos, light / 27);
    }

    public static void setBlockState(Chunk chunk, BlockPos pos, IBlockState state) {
        World world = chunk.getWorld();
        if (!world.isRemote && state.getBlock() == Blocks.LOG)
            System.out.println("adding log");
    }
}
