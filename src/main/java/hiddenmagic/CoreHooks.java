package hiddenmagic;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

// Hooks are called by hiddenmagiccore.
public class CoreHooks {

    public static void leafTick(BlockLeaves block, World world, BlockPos pos, IBlockState state, Random rand) {
        System.out.println("Hooked updateTick");
    }

}
