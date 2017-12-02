package hiddenmagic;

import hiddenmagic.common.CWorld;
import hiddenmagic.magictype.MagicTypes;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

// Hooks are called by hiddenmagiccore.
public class CoreHooks {
    // HOOK
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
}
